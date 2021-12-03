package collaborate.api.datasource.nft.catalog;

import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.nft.model.metadata.AssetDataCatalog;
import collaborate.api.datasource.nft.model.metadata.DatasourceLink;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.datasource.nft.model.storage.TokenMetadata;
import collaborate.api.ipfs.IpfsService;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NftDatasourceService {

  private final CatalogService catalogService;
  private final IpfsService ipfsService;
  private final TokenMetadataDAO tokenMetadataDAO;
  private final TraefikProviderService traefikProviderService;

  /**
   * @return true if at least one configuration has been saved
   */
  public boolean saveGatewayConfigurationByTokenId(Integer tokenId, String smartContract) {
    return streamByTokenId(tokenId, smartContract)
        .anyMatch(traefikProviderService::save);
  }

  public Stream<Datasource> streamByTokenId(Integer tokenId, String smartContract) {
    var tokenMetadataOpt = tokenMetadataDAO.findById(tokenId, smartContract);
    catalogService.findCatalogByTokenId(tokenId, smartContract);
    return tokenMetadataOpt
        .map(TokenMetadata::getIpfsUri)
        .map(uri -> ipfsService.cat(uri, TZip21Metadata.class))
        .flatMap(TZip21Metadata::getAssetDataCatalogUri)
        .map(catalogUri -> ipfsService.cat(catalogUri, AssetDataCatalog.class))
        .map(AssetDataCatalog::getDatasources)
        .stream()
        .flatMap(Collection::stream)
        .map(DatasourceLink::getUri)
        .map(dsUri -> ipfsService.cat(dsUri, Datasource.class));
  }
}