package collaborate.api.datasource.businessdata.document;

import static java.util.stream.Collectors.toList;

import collaborate.api.datasource.DatasourceMetadataService;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.businessdata.document.model.DownloadDocument;
import collaborate.api.datasource.businessdata.document.model.ScopeAssetDTO;
import collaborate.api.datasource.businessdata.document.model.ScopeAssetsDTO;
import collaborate.api.datasource.businessdata.find.FindBusinessDataService;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.gateway.GatewayResourceDTO;
import collaborate.api.datasource.gateway.GatewayUrlService;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.model.scope.AssetScope;
import collaborate.api.datasource.nft.AssetScopeDAO;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.datasource.nft.model.AssetDataCatalogDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.http.HttpClientFactory;
import collaborate.api.user.metadata.UserMetadataService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@Service
public class AssetsService {

  public static final String ASSET_ID_SEPARATOR = ":";

  private final AccessTokenProvider accessTokenProvider;
  private final AssetScopeDAO assetScopeDAO;
  private final String businessDataContractAddress;
  private final Clock clock;
  private final DatasourceService datasourceService;
  private final DatasourceMetadataService datasourceMetadataService;
  private final FindBusinessDataService findBusinessDataService;
  private final GatewayUrlService gatewayUrlService;
  private final HttpClientFactory httpClientFactory;
  private final UserMetadataService userMetadataService;
  private final CatalogService catalogService;
  private final ObjectMapper objectMapper;

  public Optional<ScopeAssetsDTO> listScopeAssets(Integer tokenId) {
    var catalogOpt = catalogService.findCatalogByTokenId(
        tokenId,
        businessDataContractAddress
    );

    return catalogOpt
        .map(AssetDataCatalogDTO::getDatasources)
        .stream()
        .flatMap(Collection::stream)
        .map(this::listScopeAssets)
        .flatMap(Optional::stream)
        .findFirst();
  }

  /**
   * @return The datasource response for the given resource
   */
  public Optional<ScopeAssetsDTO> listScopeAssets(AssetDetailsDatasourceDTO datasourceDTO) {
    var datasourceId = datasourceDTO.getId();
    var resourceAlias = datasourceDTO.getAssetIdForDatasource();
    var resourceResponse = getAssetListResponse(datasourceId, resourceAlias);
    if (!resourceResponse.getStatusCode().is2xxSuccessful()) {
      log.error("Can't get asset list for datasourceId={} and resourceAlias={}", datasourceId,
          resourceAlias);
      throw new ResponseStatusException(resourceResponse.getStatusCode());
    }

    return Optional.ofNullable(resourceResponse.getBody())
        .map(JsonNode::toString)
        .map(assetListJsonString -> ScopeAssetsDTO.builder()
            .accessStatus(findBusinessDataService.getAccessStatus(datasourceId, resourceAlias))
            .datasourceId(datasourceId)
            .providerAddress(datasourceDTO.getOwnerAddress())
            .scopeName(resourceAlias)
            .assets(convertJsonToScopeAssetDTOs(assetListJsonString, datasourceId,
                resourceAlias).collect(toList()))
            .build());
  }

  ResponseEntity<JsonNode> getAssetListResponse(String datasourceId, String alias) {
    var gatewayResource = GatewayResourceDTO.builder()
        .datasourceId(datasourceId)
        .scope(alias)
        .build();
    return gatewayUrlService.fetch(gatewayResource);
  }

  Optional<AccessTokenResponse> getJwt(String datasourceId, String resource) {
    return getOAuth2(datasourceId)
        .map(oAuth2 -> getOwnerAccessToken(datasourceId, oAuth2, resource))
        .or(() -> getRequesterAccessToken(datasourceId, resource));
  }

  Optional<OAuth2ClientCredentialsGrant> getOAuth2(String datasourceId) {
    return userMetadataService.find(datasourceId, VaultMetadata.class)
        .filter(VaultMetadata::hasOAuth2)
        .map(VaultMetadata::getOAuth2);
  }

  private AccessTokenResponse getOwnerAccessToken(String datasourceId,
      OAuth2ClientCredentialsGrant auth2,
      String resource) {
    var scope = assetScopeDAO.findById(datasourceId + ":" + resource).map(AssetScope::getScope);
    return accessTokenProvider.get(auth2, scope);
  }

  private Optional<AccessTokenResponse> getRequesterAccessToken(String datasourceId, String scope) {
    return userMetadataService
        .find(datasourceId + ASSET_ID_SEPARATOR + scope, VaultMetadata.class)
        .filter(VaultMetadata::hasJwt)
        .map(VaultMetadata::getJwt)
        .map(accessToken -> AccessTokenResponse.builder().accessToken(accessToken).build());
  }

  Stream<ScopeAssetDTO> convertJsonToScopeAssetDTOs(String jsonResponse, String datasourceId,
      String resourceAlias) {
    var metadata = datasourceService.findById(datasourceId)
        .map(d -> datasourceMetadataService.findByAlias(d.getContent(), resourceAlias))
        .orElse(Collections.emptyMap());

    var idPath = Optional.ofNullable(metadata.get("id.jsonPath"))
        .map(JSONPath::compile)
        .orElse(JSONPath.compile("$.title"));

    var downloadLink = Optional.ofNullable(metadata.get("downloadLink"));
    var downloadPath = Optional.ofNullable(metadata.get("download.jsonPath"))
        .map(JSONPath::compile)
        .orElse(JSONPath.compile("$.uri"));

    JSONArray results;
    try {
      results = objectMapper.readValue(jsonResponse, JSONArray.class);
    } catch (JsonProcessingException e) {
      log.error("While reading jsonResponse={}", jsonResponse);
      throw new IllegalStateException(e);
    }
    return results.stream()
        .map(r -> ScopeAssetDTO.builder()
            .name(idPath.eval(r, String.class))
            .type("MVP document")
            .synchronizedDate(ZonedDateTime.now(clock))
            .downloadLink(
                URI.create(downloadLink.map(
                        d -> StringUtils.replace(d, "$id", idPath.eval(r, String.class)))
                    .orElse(downloadPath.eval(r, String.class))
                )
            ).build()
        );
  }

  public ZipOutputStream download(ScopeAssetsDTO scopeAssets, ServletOutputStream outputStream)
      throws IOException {
    var accessTokenResponse = getJwt(scopeAssets.getDatasourceId(), scopeAssets.getScopeName())
        .orElseThrow(() ->   new ResponseStatusException(HttpStatus.PROXY_AUTHENTICATION_REQUIRED));
    var r = scopeAssets.getAssets().stream()
        .map(ScopeAssetDTO::getDownloadLink)
        .map(URI::toString)
        .map(s -> fetch(s, accessTokenResponse))
        .collect(toList());
    return zip(r, outputStream);
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
        Files.delete(downloadDocument.getFile().toPath());
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
        downloadResponseToDownloadDocument);
  }


  private final ResponseExtractor<DownloadDocument> downloadResponseToDownloadDocument =
      httpResponse -> {
        var filename = Optional.ofNullable(
                httpResponse.getHeaders().get("Content-Disposition")
            ).stream()
            .flatMap(Collection::stream)
            .findFirst()
            .map(contentDisposition -> contentDisposition.substring(
                    contentDisposition.indexOf("\"") + 1,
                    contentDisposition.lastIndexOf("\"")
                )
            ).orElse(buildDownloadFilename());

        File ret = File.createTempFile(filename, "");
        StreamUtils.copy(httpResponse.getBody(), new FileOutputStream(ret));

        return new DownloadDocument(filename, ret);
      };

  private String buildDownloadFilename(){
    var dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    return dateFormatter.format(ZonedDateTime.now(clock));
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

  public ResponseEntity<String> testConnection(Integer tokenId) {
    var assetsDTO = listScopeAssets(tokenId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    var accessTokenResponse = getJwt(assetsDTO.getDatasourceId(), assetsDTO.getScopeName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.PROXY_AUTHENTICATION_REQUIRED));


    var downloadLink = assetsDTO.getAssets().stream()
        .findFirst()
        .map(ScopeAssetDTO::getDownloadLink)
        .map(URI::toString)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    RestTemplate restTemplate = buildRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessTokenResponse.getAccessToken());

    return restTemplate.exchange(
        downloadLink,
        HttpMethod.GET,
        new HttpEntity<String>(null, headers),
        String.class);
  }
}
