package collaborate.api.datasource.businessdata.document;

import static java.util.stream.Collectors.toList;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.DatasourceMetadataService;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.businessdata.document.model.BusinessDataDocument;
import collaborate.api.datasource.businessdata.document.model.BusinessDataNFTSummary;
import collaborate.api.datasource.businessdata.document.model.DownloadDocument;
import collaborate.api.datasource.businessdata.document.model.ScopeAssetsDTO;
import collaborate.api.datasource.businessdata.find.AssetDetailsService;
import collaborate.api.datasource.gateway.GatewayResourceDTO;
import collaborate.api.datasource.gateway.GatewayUrlService;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.http.HttpClientFactory;
import collaborate.api.tag.TagService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
  private final AssetDetailsService assetDetailsService;
  private final AuthenticationService authenticationService;
  private final String businessDataContractAddress;
  private final CatalogService catalogService;
  private final Clock clock;
  private final DatasourceService datasourceService;
  private final DatasourceMetadataService datasourceMetadataService;
  private final GatewayUrlService gatewayUrlService;
  private final HttpClientFactory httpClientFactory;
  private final ObjectMapper objectMapper;
  private final TagService tagService;

  public BusinessDataNFTSummary getSummary(Integer tokenId) {
    var catalog = catalogService.getCatalogByTokenId(tokenId, businessDataContractAddress);
    return catalog.getDatasources()
        .stream()
        .map(d -> buildSummary(d, tokenId))
        .findFirst()
        .orElseThrow(
            () -> new IllegalStateException("No catalog found for tokenId=" + tokenId));
  }

  public BusinessDataNFTSummary buildSummary(AssetDetailsDatasourceDTO details, Integer nftId) {
    var datasourceId = details.getId();
    var resourceAlias = details.getAssetIdForDatasource();

    var summaryBuilder = BusinessDataNFTSummary.builder()
        .accessStatus(assetDetailsService.getAccessStatus(datasourceId, nftId))
        .datasourceId(datasourceId)
        .providerAddress(details.getOwnerAddress())
        .scopeName(resourceAlias);

    tagService.findTokenMetadataTzktUrl(businessDataContractAddress)
        .ifPresent(summaryBuilder::blockchainExplorerPreview);

    return summaryBuilder.build();
  }

  public Page<BusinessDataDocument> listScopeAssets(Integer tokenId, Pageable pageable) {
    return catalogService.getCatalogByTokenId(
            tokenId,
            businessDataContractAddress
        ).getDatasources()
        .stream()
        .findFirst()
        .map(this::listFrom)
        .map(assets -> new PageImpl<>(
            assets.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(toList()),
            pageable,
            assets.size()))
        .orElseGet(() -> new PageImpl<>(Collections.emptyList(), pageable, 0));
  }

  /**
   * @return The datasource response for the given resource
   */
  public List<BusinessDataDocument> listFrom(AssetDetailsDatasourceDTO datasourceDTO) {
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
        .map(assetListJsonString ->
            convertJsonToBusinessDataDocument(assetListJsonString, datasourceId, resourceAlias)
                .collect(toList())
        ).orElse(Collections.emptyList());
  }

  ResponseEntity<JsonNode> getAssetListResponse(String datasourceId, String alias) {
    var gatewayResource = GatewayResourceDTO.builder()
        .datasourceId(datasourceId)
        .alias(alias)
        .build();
    return gatewayUrlService.fetch(gatewayResource);
  }

  Stream<BusinessDataDocument> convertJsonToBusinessDataDocument(String jsonResponse,
      String datasourceId,
      String resourceAlias) {
    var metadata = datasourceService.findById(datasourceId)
        .map(d -> datasourceMetadataService.findByAlias(d, resourceAlias))
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
        .map(r -> BusinessDataDocument.builder()
            .name(idPath.eval(r, String.class))
            .type("MVP document")
            .synchronizedDate(ZonedDateTime.now(clock))
            .downloadLink(
                URI.create(downloadLink.map(
                        d -> StringUtils.replace(d, "$id", idPath.eval(r, String.class)))
                    .orElseGet(() -> downloadPath.eval(r, String.class))
                )
            ).build()
        );
  }

  public void download(ScopeAssetsDTO scopeAssets, ServletOutputStream outputStream)
      throws IOException {
    var jwt = authenticationService.getJwt(scopeAssets.getNftId(), businessDataContractAddress);
    var r = scopeAssets.getAssets().stream()
        .map(BusinessDataDocument::getDownloadLink)
        .map(URI::toString)
        .map(s -> download(s, jwt))
        .collect(toList());
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
        Files.delete(downloadDocument.getFile().toPath());
      }
      return zipOutputStream;
    }
  }

  public DownloadDocument download(String url, String oAuth2Jwt) {
    RestTemplate restTemplate = buildRestTemplate();

    return restTemplate.execute(
        url,
        HttpMethod.GET,
        clientHttpRequest -> clientHttpRequest.getHeaders().set(
            "Authorization",
            "Bearer " + oAuth2Jwt
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

  private String buildDownloadFilename() {
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

  public ResponseEntity<String> fetch(Integer tokenId, Optional<String> assetIdOpt) {
    var assetDTOs = catalogService.getCatalogByTokenId(
            tokenId,
            businessDataContractAddress
        ).getDatasources().stream()
        .findFirst()
        .map(this::listFrom)
        .orElseThrow(() -> new IllegalStateException("No datasource for tokenId=" + tokenId));

    var downloadLink = assetIdOpt.map(
            assetId -> assetDTOs.stream()
                .filter(assetDTO -> StringUtils.equals(assetDTO.getName(), assetId))
                .findFirst()
        ).orElseGet(
            () -> assetDTOs.stream().findFirst()
        ).map(BusinessDataDocument::getDownloadLink)
        .map(URI::toString)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

    var jwt = authenticationService.getJwt(tokenId, businessDataContractAddress);

    RestTemplate restTemplate = buildRestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + jwt);

    return restTemplate.exchange(
        downloadLink,
        HttpMethod.GET,
        new HttpEntity<String>(null, headers),
        String.class);
  }

}
