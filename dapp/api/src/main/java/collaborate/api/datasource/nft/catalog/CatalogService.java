package collaborate.api.datasource.nft.catalog;

import static java.lang.String.format;

import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.nft.TokenDAO;
import collaborate.api.datasource.nft.model.AssetDataCatalogDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.datasource.nft.model.metadata.AssetDataCatalog;
import collaborate.api.datasource.nft.model.metadata.DatasourceLink;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.datasource.nft.model.storage.TokenMetadata;
import collaborate.api.ipfs.IpfsService;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CatalogService {

  private final IpfsService ipfsService;
  private final DatasourceService datasourceService;
  private final TokenDAO tokenMetadataDAO;
  private final TraefikProviderService traefikProviderService;


  public AssetDataCatalogDTO getCatalogByTokenId(Integer tokenId, String smartContract) {
    var tokenMedataOpt = tokenMetadataDAO.findById(tokenId, smartContract);
    return tokenMedataOpt
        .map(TokenMetadata::getIpfsUri)
        .flatMap(this::findByIpfsLink)
        .orElseThrow(() -> new IllegalStateException(
            format("No catalog found for nftId=%d, smartContract=%s", tokenId, smartContract)
        ));
  }

  public Optional<AssetDataCatalogDTO> findByIpfsLink(String tZip21Url) {
    try {
      var tokenMetadata = ipfsService.cat(tZip21Url, TZip21Metadata.class);
      return getAssetDataCatalogDTO(tokenMetadata);
    } catch (Exception e) {
      log.error("While getting dataCatalog from tZip21Url={}\n{}", tZip21Url, e);
      return Optional.empty();
    }
  }

  public Optional<AssetDataCatalogDTO> getAssetDataCatalogDTO(TZip21Metadata tokenMetadata) {
    if (tokenMetadata == null) {
      return Optional.empty();
    }
    return tokenMetadata.getAssetDataCatalogUri()
        .map(catalogUri -> ipfsService.cat(catalogUri, AssetDataCatalog.class))
        .map(AssetDataCatalog::getDatasources)
        .map(
            links -> links.stream()
                .map(this::buildDatasourceDTO)
                .collect(Collectors.toList())
        ).map(AssetDataCatalogDTO::new);
  }

  AssetDetailsDatasourceDTO buildDatasourceDTO(DatasourceLink datasourceLink) {
    var datasource = ipfsService.cat(datasourceLink.getUri(), Datasource.class);
    return AssetDetailsDatasourceDTO.builder()
        .id(datasourceLink.getId())
        .assetIdForDatasource(datasourceLink.getAssetIdForDatasource())
        .baseUri(traefikProviderService.buildDatasourceBaseUri(datasource))
        .ownerAddress(datasource.getOwner())
        .scopes(
            datasourceService.getResourcesByDataSourceId(datasourceLink.getId())
                .orElseGet(() -> {
                  log.warn("No resources found for datasource={}", datasourceLink.getId());
                  return Collections.emptySet();
                })
        ).build();
  }

}
