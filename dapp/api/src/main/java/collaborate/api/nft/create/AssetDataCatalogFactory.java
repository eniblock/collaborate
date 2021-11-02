package collaborate.api.nft.create;

import static collaborate.api.ipfs.IpfsService.IPFS_PROTOCOL_PREFIX;

import collaborate.api.datasource.DatasourceService;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.nft.TokenMetadataProperties;
import collaborate.api.nft.model.metadata.AssetDataCatalog;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssetDataCatalogFactory {

  private final Clock clock;
  private final DatasourceService datasourceService;
  private final DateFormatterFactory dateFormatterFactory;
  private final TokenMetadataProperties tokenMetadataProperties;

  Path buildRelativePathForAssetId(AssetDTO assetDTO) {
    return Path.of(
        assetDTO.getAssetType(),
        dateFormatterFactory
            .forPattern(tokenMetadataProperties.getAssetDataCatalogPartitionDatePattern()),
        // ms is added to prevent multiple creation on the same asset collision,
        //could occurs if a creation is made by providing an invalid asset Id already used on the same partition
        assetDTO.getAssetId() + "_" + clock.millis()
    );
  }

  AssetDataCatalog create(AssetDTO assetDTO) {
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
