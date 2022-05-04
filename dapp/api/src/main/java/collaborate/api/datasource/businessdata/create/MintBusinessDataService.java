package collaborate.api.datasource.businessdata.create;

import static collaborate.api.datasource.businessdata.document.ScopeAssetsService.ASSET_ID_SEPARATOR;
import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.ATTR_NAME_ALIAS;
import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.ATTR_NAME_TEST_CONNECTION;
import static java.lang.String.format;

import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.nft.catalog.create.AssetDTO;
import collaborate.api.datasource.nft.catalog.create.Tzip21MetadataService;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class MintBusinessDataService {

  private final BusinessDataTokenMetadataSupplier tokenMetadataSupplier;
  private final CreateBusinessDataNftDAO createBusinessDataNftDAO;
  private final Tzip21MetadataService tzip21MetadataService;

  public void mint(DatasourceDTO datasourceDTO) {
    // Use a visitor for Access and for Datasource type when new datasource type will be implemented
    if (isOAuth2WebServer(datasourceDTO)) {
      var webServerDatasourceDTO = (WebServerDatasourceDTO) datasourceDTO;
      var assetIdAndUris = webServerDatasourceDTO.getResources().stream()
          .filter(r -> !r.keywordsContainsName(ATTR_NAME_TEST_CONNECTION))
          .filter(r -> r.keywordsContainsName(ATTR_NAME_ALIAS))
          .map(resource -> buildAssetDto(datasourceDTO.getId(), resource))
          .map(this::buildAssetIdAndMetadataUri)
          .collect(Collectors.toList());
      createBusinessDataNftDAO.mintBusinessDataNFT(assetIdAndUris);
    } else {
      throw new NotImplementedException("Only Oauth2 is implemented for Business data");
    }
  }

  private AssetDTO buildAssetDto(UUID dataSourceUUID, WebServerResource webServerResource) {
    var alias = webServerResource.findFirstKeywordValueByName(ATTR_NAME_ALIAS)
        .orElseThrow(() -> new IllegalStateException(
            format("Missing keyword with name=%s", ATTR_NAME_ALIAS)));
    return AssetDTO.builder()
        .displayName(webServerResource.getDescription())
        .assetId(dataSourceUUID + ASSET_ID_SEPARATOR + alias)
        .assetType("business-data")
        .datasourceUUID(dataSourceUUID)
        .assetIdForDatasource(alias)
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
