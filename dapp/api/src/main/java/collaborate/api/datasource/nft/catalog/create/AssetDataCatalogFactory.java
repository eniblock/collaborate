package collaborate.api.datasource.nft.catalog.create;

import static collaborate.api.ipfs.IpfsService.IPFS_PROTOCOL_PREFIX;

import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.nft.model.metadata.AssetDataCatalog;
import collaborate.api.datasource.nft.model.metadata.DatasourceLink;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetDataCatalogFactory {

  private final DatasourceService datasourceService;

  public AssetDataCatalog create(AssetDTO assetDTO) {
    var datasourceInIpfs = datasourceService
        .findById(assetDTO.getDatasourceUUID().toString())
        .orElseThrow(
            () -> new NoSuchElementException(assetDTO.getDatasourceUUID().toString())
        );
    var datasourceRef = DatasourceLink.builder()
        .id(assetDTO.getDatasourceUUID().toString())
        .uri(IPFS_PROTOCOL_PREFIX + datasourceInIpfs.getCid())
        .assetIdForDatasource(assetDTO.getAssetIdForDatasource())
        .build();
    return AssetDataCatalog.builder()
        .datasources(List.of(datasourceRef))
        .build();
  }

}
