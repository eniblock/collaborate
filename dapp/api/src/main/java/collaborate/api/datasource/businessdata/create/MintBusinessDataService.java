package collaborate.api.datasource.businessdata.create;

import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.ATTR_NAME_ALIAS;
import static collaborate.api.datasource.model.dto.web.Attribute.ATTR_JWT_SCOPE;
import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.ATTR_NAME_TEST_CONNECTION;
import static java.lang.String.format;

import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.model.AssetScope;
import collaborate.api.datasource.model.AssetScopeId;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.nft.AssetScopeRepository;
import collaborate.api.datasource.nft.TokenMetadataProperties;
import collaborate.api.datasource.nft.catalog.create.AssetDTO;
import collaborate.api.datasource.nft.catalog.create.Tzip21MetadataService;
import collaborate.api.date.DateFormatterFactory;
import java.io.IOException;
import java.nio.file.Path;
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

  private final AssetScopeRepository assetScopeRepository;
  private final CreateBusinessDataNftDAO createBusinessDataNftDAO;
  private final DateFormatterFactory dateFormatterFactory;
  private final TokenMetadataProperties tokenMetadataProperties;
  private final TZip21MetadataFactory tZip21MetadataFactory;
  private final Tzip21MetadataService tzip21MetadataService;
  private final UUIDGenerator uuidGenerator;


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

    // FIXME move to a dedicated service
    webServerResource.findFirstKeywordValueByName(ATTR_JWT_SCOPE)
        .ifPresent(scope -> assetScopeRepository.save(
                new AssetScope(
                    new AssetScopeId(dataSourceUUID.toString(), alias),
                    scope,
                    null)
            )
        );

    return AssetDTO.builder()
        .assetRelativePath(buildAssetRelativePath())
        .assetType("business-data")
        .datasourceUUID(dataSourceUUID)
        .assetIdForDatasource(alias)
        .tZip21Metadata(tZip21MetadataFactory.create(webServerResource))
        .build();
  }


  String buildAssetRelativePath() {
    return Path.of(
        dateFormatterFactory.forPattern(
            tokenMetadataProperties.getAssetDataCatalogPartitionDatePattern()
        ),
        uuidGenerator.randomUUID().toString()
    ).toString();
  }

  private AssetIdAndUri buildAssetIdAndMetadataUri(AssetDTO assetDTO) {
    try {
      log.info("Minting asset={}", assetDTO);
      var ipfsMetadataUri = tzip21MetadataService.saveMetadata(assetDTO);
      return new AssetIdAndUri(
          assetDTO.getDatasourceUUID() + ":" + assetDTO.getAssetIdForDatasource(),
          ipfsMetadataUri);
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
