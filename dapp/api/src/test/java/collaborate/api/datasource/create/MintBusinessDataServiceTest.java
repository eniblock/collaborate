package collaborate.api.datasource.create;

import static collaborate.api.datasource.businessdata.document.ScopeAssetsService.ASSET_ID_SEPARATOR;
import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.SCOPE_PREFIX;
import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.TEST_CONNECTION;

import collaborate.api.datasource.businessdata.create.AssetIdAndUri;
import collaborate.api.datasource.businessdata.create.BusinessDataTokenMetadataSupplier;
import collaborate.api.datasource.businessdata.create.CreateBusinessDataNftDAO;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.nft.catalog.create.AssetDTO;
import collaborate.api.datasource.nft.catalog.create.Tzip21MetadataService;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class MintBusinessDataServiceTest {

  private final BusinessDataTokenMetadataSupplier tokenMetadataSupplier;
  private final CreateBusinessDataNftDAO createBusinessDataNftDAO;
  private final Tzip21MetadataService tzip21MetadataService;

  public void mint(DatasourceDTO datasourceDTO) {
    // Use a visitor for Access and for Datasource type when new datasource type will be implemented
    if (isOAuth2WebServer(datasourceDTO)) {
      var webServerDatasourceDTO = (WebServerDatasourceDTO) datasourceDTO;
      var assetIdAndUris = webServerDatasourceDTO.getResources().stream()
          .filter(r -> !r.keywordsContain(TEST_CONNECTION))
          .map(resource -> resource.findFirstKeywordRemovingPrefix(SCOPE_PREFIX))
          .flatMap(Optional::stream)
          .map(scope -> buildAssetDto(datasourceDTO.getId(), scope))
          .map(this::buildAssetIdAndMetadataUri)
          .collect(Collectors.toList());
      createBusinessDataNftDAO.mintBusinessDataNFT(assetIdAndUris);
    } else {
      throw new NotImplementedException("Only Oauth2 is implemented for Business data");
    }
  }

  private AssetDTO buildAssetDto(UUID dataSourceUUID, String scope) {
    return AssetDTO.builder()
        .assetId(dataSourceUUID + ASSET_ID_SEPARATOR + scope)
        .assetType("business-data")
        .datasourceUUID(dataSourceUUID)
        .assetIdForDatasource(scope)
        .build();
  }

  private AssetIdAndUri buildAssetIdAndMetadataUri(AssetDTO assetDTO) {
    try {
      log.info("Minting asset={}", assetDTO);
      var ipfsMetadataUri = tzip21MetadataService.saveMetadata(assetDTO, tokenMetadataSupplier);
      return new AssetIdAndUri(assetDTO.getAssetId(), ipfsMetadataUri);
    } catch (IOException e) {
      log.error("error while minting asset={}", assetDTO);
      throw new IllegalStateException(e);
    }
  }

  private boolean isOAuth2WebServer(DatasourceDTO datasourceDTO) {
    return datasourceDTO instanceof WebServerDatasourceDTO
        && datasourceDTO.getAuthMethod() instanceof OAuth2ClientCredentialsGrant;
  }

}
