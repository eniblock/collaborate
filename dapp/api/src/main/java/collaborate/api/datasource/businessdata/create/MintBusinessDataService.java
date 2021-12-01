package collaborate.api.datasource.businessdata.create;

import static collaborate.api.datasource.businessdata.document.ScopeAssetsService.ASSET_ID_SEPARATOR;

import collaborate.api.datasource.AssetListService;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.nft.catalog.create.AssetDTO;
import collaborate.api.datasource.nft.catalog.create.Tzip21MetadataService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class MintBusinessDataService {

  private final AssetListService assetListService;
  private final BusinessDataTokenMetadataSupplier tokenMetadataSupplier;
  private final CreateBusinessDataNftDAO createBusinessDataNftDAO;
  private final Tzip21MetadataService tzip21MetadataService;

  public void mint(DatasourceDTO datasourceDTO) {
    if (isOAuth2WebServer(datasourceDTO)) {
      var assetListResponse = assetListService.getAssetListResponse(
          (WebServerDatasourceDTO) datasourceDTO);
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
      var ipfsMetadataUri = tzip21MetadataService.saveMetadata(assetDTO, tokenMetadataSupplier);
      return new AssetIdAndUri(assetDTO.getAssetId(), ipfsMetadataUri);
    } catch (IOException e) {
      log.error("error while minting asset={}", assetDTO);
      throw new IllegalStateException(e);
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
        && datasourceDTO.getAuthMethod() instanceof OAuth2ClientCredentialsGrant;
  }

}
