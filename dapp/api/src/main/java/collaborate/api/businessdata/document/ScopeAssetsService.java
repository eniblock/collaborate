package collaborate.api.businessdata.document;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.SCOPE_ASSET_LIST;
import static java.util.stream.Collectors.toList;

import collaborate.api.businessdata.document.model.DownloadDocument;
import collaborate.api.businessdata.document.model.ScopeAssetDTO;
import collaborate.api.businessdata.document.model.ScopeAssetsDTO;
import collaborate.api.businessdata.find.FindBusinessDataService;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.AccessTokenProvider;
import collaborate.api.datasource.create.provider.traefik.TraefikProviderService;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.gateway.GatewayResourceDTO;
import collaborate.api.gateway.GatewayUrlService;
import collaborate.api.http.HttpClientFactory;
import collaborate.api.nft.find.TokenMetadataService;
import collaborate.api.passport.model.AssetDataCatalogDTO;
import collaborate.api.passport.model.DatasourceDTO;
import collaborate.api.user.metadata.UserMetadataService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@Service
public class ScopeAssetsService {

  public static final String ASSET_ID_SEPARATOR = ":";
  private final AccessTokenProvider accessTokenProvider;
  private final ApiProperties apiProperties;
  private final Clock clock;
  private final FindBusinessDataService findBusinessDataService;
  private final GatewayUrlService gatewayUrlService;
  private final HttpClientFactory httpClientFactory;
  private final UserMetadataService userMetadataService;
  private final TokenMetadataService tokenMetadataService;
  private final TraefikProviderService traefikProviderService;

  public Optional<ScopeAssetsDTO> listScopeAssets(Integer tokenId) throws InterruptedException {
    var catalogOpt = tokenMetadataService.findByTokenId(
        tokenId,
        apiProperties.getBusinessDataContractAddress()
    );

    fetchMissingDatasourcesConfiguration(tokenId, catalogOpt);

    return catalogOpt
        .map(AssetDataCatalogDTO::getDatasources)
        .stream()
        .flatMap(Collection::stream)
        .map(d -> listScopeAssets(d))
        .findFirst();
  }

  private void fetchMissingDatasourcesConfiguration(
      Integer tokenId,
      Optional<AssetDataCatalogDTO> catalogOpt
  ) throws InterruptedException {
    catalogOpt
        .map(AssetDataCatalogDTO::getDatasources)
        .stream()
        .flatMap(Collection::stream)
        .forEach(datasourceDTO -> fetchIfMissing(datasourceDTO, tokenId));
    Thread.sleep(1000);
  }

  public void fetchIfMissing(DatasourceDTO datasourceDTO, Integer tokenId) {
    if (!traefikProviderService.exists(datasourceDTO.getId())) {
      traefikProviderService.fetchByTokenId(tokenId,
          apiProperties.getBusinessDataContractAddress());
    }
  }

  public ScopeAssetsDTO listScopeAssets(DatasourceDTO datasourceDTO) {
    var datasourceId = datasourceDTO.getId();
    var scope = datasourceDTO.getAssetIdForDatasource();
    var scopesResponse = getAssetListResponse(datasourceId);
    if (!scopesResponse.getStatusCode().is2xxSuccessful()) {
      log.error("Can't get asset list for datasourceId={} and scope={}", datasourceId, scope);
      throw new ResponseStatusException(scopesResponse.getStatusCode());
    }

    String assetListJsonString = scopesResponse.getBody().toString();

    return ScopeAssetsDTO.builder()
        .accessStatus(findBusinessDataService.getAccessStatus(datasourceId, scope))
        .datasourceId(datasourceId)
        .providerAddress(datasourceDTO.getOwnerAddress())
        .scopeName(scope)
        .assets(filterByScope(assetListJsonString, scope).collect(toList()))
        .build();
  }

  Optional<AccessTokenResponse> getJwt(String datasourceId, String scope) {
    var oAuthScope = StringUtils.removeStart(scope, "scope:");
    return getOAuth2(datasourceId)
        .map(oAuth2 -> getOwnerAccessToken(oAuth2, oAuthScope))
        .or(() -> getRequesterAccessToken(datasourceId, oAuthScope));
  }

  Optional<OAuth2> getOAuth2(String datasourceId) {
    return userMetadataService.find(datasourceId, VaultMetadata.class)
        .filter(VaultMetadata::hasOAuth2)
        .map(VaultMetadata::getOAuth2);
  }

  private AccessTokenResponse getOwnerAccessToken(OAuth2 auth2, String scope) {
    return accessTokenProvider.get(auth2, Optional.of(scope));
  }

  private Optional<AccessTokenResponse> getRequesterAccessToken(String datasourceId, String scope) {
    return userMetadataService
        .find(datasourceId + ASSET_ID_SEPARATOR + scope, VaultMetadata.class)
        .filter(VaultMetadata::hasJwt)
        .map(VaultMetadata::getJwt)
        .map(accessToken -> AccessTokenResponse.builder().accessToken(accessToken).build());
  }

  ResponseEntity<JsonNode> getAssetListResponse(String datasourceId) {
    var gatewayResource = GatewayResourceDTO.builder()
        .datasourceId(datasourceId)
        .scope(SCOPE_ASSET_LIST)
        .build();
    return gatewayUrlService.fetch(
        gatewayResource,
        Optional.empty()
    );
  }

  Stream<ScopeAssetDTO> filterByScope(String jsonResponse, String scope) {
    var resourcesPath = JSONPath.compile("$._embedded.metadatas");
    var namePath = JSONPath.compile("$.title");
    var linkPath = JSONPath.compile("$._links.self.href");
    var downloadPath = JSONPath.compile("$._links.download.href");
    var scopePath = JSONPath.compile("$.scope");
    if (resourcesPath.contains(jsonResponse)) {
      var docs = resourcesPath.<JSONArray>eval(jsonResponse, JSONArray.class);
      return docs.stream()
          .filter(scopePath::contains)
          .filter(d -> scope.equals(scopePath.eval(d, String.class)))
          .map(d -> ScopeAssetDTO.builder()
              .name(namePath.eval(d, String.class))
              .type("MVP document")
              .synchronizedDate(ZonedDateTime.now(clock))
              .link(URI.create(linkPath.eval(d, String.class)))
              .downloadLink(URI.create(downloadPath.eval(d, String.class)))
              .build());
    }
    return Stream.empty();
  }

  public ZipOutputStream download(ScopeAssetsDTO scopeAssets, ServletOutputStream outputStream)
      throws IOException {
    var accessTokenResponseOpt = getJwt(scopeAssets.getDatasourceId(), scopeAssets.getScopeName());
    if (accessTokenResponseOpt.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    var r = scopeAssets.getAssets().stream()
        .map(ScopeAssetDTO::getDownloadLink)
        .map(URI::toString)
        .map(s -> fetch(s, accessTokenResponseOpt.get()))
        .collect(toList());
    return
        zip(r, outputStream);
  }

  public ZipOutputStream zip(List<DownloadDocument> documents, ServletOutputStream outputStream)
      throws IOException {
    try (ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {

      Set<String> fileNameSet = new LinkedHashSet<>();

      for (DownloadDocument downloadDocument : documents) {
        String fileName = downloadDocument.getFileName();
        for (int i = 1; !fileNameSet.add(fileName); i++) {
          // If the value is already present in the set, an index is added
          fileName = downloadDocument.getFileName() + '_' + i;
          log.info("Identical file name found : " + fileName);
        }
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        try (FileInputStream fileInputStream = new FileInputStream(downloadDocument.getFile())) {
          IOUtils.copy(fileInputStream, zipOutputStream);
          zipOutputStream.closeEntry();
        }
        downloadDocument.getFile().delete();
      }
      return zipOutputStream;
    }
  }

  public DownloadDocument fetch(String url, AccessTokenResponse oAuth2Jwt) {
    RestTemplate restTemplate = buildRestTemplate();

    return restTemplate.execute(
        url,
        HttpMethod.GET,
        clientHttpRequest -> clientHttpRequest.getHeaders().set(
            "Authorization",
            "Bearer " + oAuth2Jwt.getAccessToken()
        ),
        httpResponse -> {
          var contentDisposition = httpResponse.getHeaders().get("Content-Disposition").get(0);
          String filename = contentDisposition.substring(
              contentDisposition.indexOf("\"") + 1,
              contentDisposition.lastIndexOf("\"")
          );

          File ret = File.createTempFile(filename, "");
          StreamUtils.copy(httpResponse.getBody(), new FileOutputStream(ret));

          return new DownloadDocument(filename, ret);
        });
  }

  private RestTemplate buildRestTemplate() {
    var restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(
        new HttpComponentsClientHttpRequestFactory(
            httpClientFactory.createTrustAllAndNoHostnameVerifier()
        )
    );

    return restTemplate;
  }
}
