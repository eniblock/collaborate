package collaborate.api.businessdata.create;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.SCOPE_ASSET_LIST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

import collaborate.api.datasource.create.RequestEntitySupplierFactory;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.nft.create.AssetDTO;
import collaborate.api.nft.create.CreateNFTService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@Service
public class CreateBusinessDataService {

  public static final String ASSET_ID_SEPARATOR = ":";
  private final CreateBusinessDataNftDAO createBusinessDataNftDAO;
  private final CreateNFTService createNFTService;
  private final ObjectMapper objectMapper;
  private final RequestEntitySupplierFactory requestEntitySupplierFactory;
  private final BusinessDataTokenMetadataSupplier tokenMetadataSupplier;

  public void create(DatasourceDTO datasourceDTO) {
    if (isOAuth2WebServer(datasourceDTO)) {
      var assetListResponse = getAssetListResponse((WebServerDatasourceDTO) datasourceDTO);
      var assetIdAndUris = getScopeFromAssetList(assetListResponse)
          .map(s -> buildAssetDto(datasourceDTO, s))
          .map(this::buildAssetIdAndUri)
          .collect(Collectors.toList());
      createBusinessDataNftDAO.mintBusinessDataNFT(assetIdAndUris);
    }
  }

  private AssetDTO buildAssetDto(DatasourceDTO datasourceDTO, String scope) {
    return AssetDTO.builder()
        .assetId(datasourceDTO.getId() + ASSET_ID_SEPARATOR + scope)
        .assetType("business-data")
        .datasourceUUID(datasourceDTO.getId())
        .assetIdForDatasource(scope)
        .build();
  }

  private AssetIdAndUri buildAssetIdAndUri(AssetDTO assetDTO) {
    try {
      log.info("Minting asset={}", assetDTO);
      var ipfsMetadataUri = createNFTService.saveMetadata(assetDTO, tokenMetadataSupplier);
      return new AssetIdAndUri(assetDTO.getAssetId(), ipfsMetadataUri);
    } catch (IOException e) {
      log.error("error while minting asset={}", assetDTO);
      throw new IllegalStateException(e);
    }
  }

  private String getAssetListResponse(WebServerDatasourceDTO webServerDatasourceDTO) {
    var assetListResponseSupplier = requestEntitySupplierFactory.create(
        webServerDatasourceDTO,
        SCOPE_ASSET_LIST
    );

    log.debug("fetching assetList for datasource={}", webServerDatasourceDTO.getId());
    var response = assetListResponseSupplier.get();
    if (response.getStatusCode() != OK) {
      throw new ResponseStatusException(
          BAD_REQUEST,
          "Getting business data documents failed with responseCode=" + response.getStatusCode()
      );
    }
    try {
      return objectMapper.writeValueAsString(response.getBody());
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(
          BAD_REQUEST,
          "Getting business data documents failed reading response",
          e
      );
    }
  }

  Stream<String> getScopeFromAssetList(String jsonResponse) {
    var resourcesPath = JSONPath.compile("$._embedded.metadatas");
    if (resourcesPath.contains(jsonResponse)) {
      var resources = resourcesPath.<JSONArray>eval(jsonResponse, JSONArray.class);
      var scopePath = JSONPath.compile("$.scope");

      return resources.stream()
          .map(scopePath::eval)
          .map(Object::toString)
          .distinct();
    }
    return Stream.empty();
  }

  private boolean isOAuth2WebServer(DatasourceDTO datasourceDTO) {
    return datasourceDTO instanceof WebServerDatasourceDTO
        && datasourceDTO.getAuthMethod() instanceof OAuth2;
  }
}
