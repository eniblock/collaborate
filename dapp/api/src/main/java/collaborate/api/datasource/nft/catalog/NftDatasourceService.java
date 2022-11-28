package collaborate.api.datasource.nft.catalog;

import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.nft.TokenDAO;
import collaborate.api.datasource.nft.model.metadata.AssetDataCatalog;
import collaborate.api.datasource.nft.model.metadata.DatasourceLink;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.ipfs.IpfsService;
import collaborate.api.tag.model.TokenMetadata;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NftDatasourceService {

  private final DatasourceService datasourceService;
  private final IpfsService ipfsService;
  private final TokenDAO tokenMetadataDAO;
  private final TraefikProviderService traefikProviderService;

  /**
   * @return true if at least one configuration has been saved
   */
  public boolean saveConfigurationByTokenId(Integer tokenId, String smartContract) {
    return streamByTokenId(tokenId, smartContract)
        .map(datasourceService::saveIfNotExists)
        .anyMatch(traefikProviderService::save);
  }

  public Stream<Datasource> streamByTokenId(Integer tokenId, String smartContract) {
    var tokenMetadataOpt = tokenMetadataDAO.findById(tokenId, smartContract);
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

  public TZip21Metadata findByIpfsLink(String tZip21Url) {
    try {      
      return ipfsService.cat(tZip21Url, TZip21Metadata.class);
    } catch (Exception e) {
      log.error("While getting tZip21Url={}\n{}", tZip21Url, e);
      return null;
    }
  }

  public Optional<TZip21Metadata> getTZip21MetadataByTokenId(Integer tokenId,
      String smartContract) {
    var tokenMetadataOpt = tokenMetadataDAO.findById(tokenId, smartContract);
    return tokenMetadataOpt
        .map(TokenMetadata::getIpfsUri)
        .map(uri -> findByIpfsLink(uri));
  }

  public Map<Integer, TZip21Metadata> getTZip21MetadataByTokenIds(Collection<Integer> tokenIdList,
      String smartContract) {
    var tokenMetadata = new HashMap<Integer, TZip21Metadata>();
    tokenIdList
        .forEach(tokenId -> tokenMetadata.put(
            tokenId,
            getTZip21MetadataByTokenId(tokenId, smartContract).orElse(null))
        );
    return tokenMetadata;
  }
}
