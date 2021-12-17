package collaborate.api.datasource.nft.catalog;

import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.nft.model.metadata.AssetDataCatalog;
import collaborate.api.datasource.nft.model.metadata.DatasourceLink;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.datasource.nft.model.storage.Multisig;
import collaborate.api.datasource.nft.model.storage.TokenMetadata;
import collaborate.api.datasource.passport.model.AssetDataCatalogDTO;
import collaborate.api.datasource.passport.model.DatasourceDTO;
import collaborate.api.ipfs.IpfsService;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CatalogService {

  private final IpfsService ipfsService;
  private final DatasourceService datasourceService;
  private final TokenMetadataDAO tokenMetadataDAO;
  private final TraefikProviderService traefikProviderService;

  public Optional<AssetDataCatalogDTO> findCatalogByTokenId(Integer tokenId, String smartContract) {
    var tokenMedataOpt = tokenMetadataDAO.findById(tokenId, smartContract);
    return tokenMedataOpt
        .map(TokenMetadata::getIpfsUri)
        .flatMap(this::findByIpfsLink);
  }

  public Optional<AssetDataCatalogDTO> findByMultisig(Multisig multisig,
      Integer multisigContractId) {
    try {
      if (multisig.getParam2() == null || StringUtils.isEmpty(multisig.getParam2().toString())) {
        log.warn("No token metadata found for multisigContractId={}", multisigContractId);
        return Optional.empty();
      } else {
        return findByIpfsLink(multisig.getParam2().toString());
      }
    } catch (Exception e) {
      log.error("While getting dataCatalog from multisigContractId={}\n{}", multisigContractId, e);
      return Optional.empty();
    }
  }

  public Optional<AssetDataCatalogDTO> findByIpfsLink(String metadataIpfsLink) {
    try {
      var tokenMetadata = ipfsService.cat(metadataIpfsLink, TZip21Metadata.class);
      return getAssetDataCatalogDTO(tokenMetadata);
    } catch (Exception e) {
      log.error("While getting dataCatalog from metadataIpfsLink={}\n{}", metadataIpfsLink, e);
      return Optional.empty();
    }
  }

  public Optional<AssetDataCatalogDTO> getAssetDataCatalogDTO(TZip21Metadata tokenMetadata) {
    return tokenMetadata.getAssetDataCatalogUri()
        .map(catalogUri -> ipfsService.cat(catalogUri, AssetDataCatalog.class))
        .map(AssetDataCatalog::getDatasources)
        .map(
            links -> links.stream()
                .map(this::buildDatasourceDTO)
                .collect(Collectors.toList())
        ).map(AssetDataCatalogDTO::new);
  }

  DatasourceDTO buildDatasourceDTO(DatasourceLink datasourceLink) {
    var datasource = ipfsService.cat(datasourceLink.getUri(), Datasource.class);
    return DatasourceDTO.builder()
        .id(datasourceLink.getId())
        .assetIdForDatasource(datasourceLink.getAssetIdForDatasource())
        .baseUri(traefikProviderService.buildDatasourceBaseUri(datasource))
        .ownerAddress(datasource.getOwner())
        .scopes(
            datasourceService.getScopesByDataSourceId(datasourceLink.getId())
                .orElseGet(() -> {
                  log.warn("No scopes found for datasource={}", datasourceLink.getId());
                  return Collections.emptySet();
                })
        ).build();
  }


}
