package collaborate.api.passport.create;

import static collaborate.api.ipfs.IpfsService.IPFS_PROTOCOL_PREFIX;
import static collaborate.api.passport.create.TokenMetadataFactory.NFT_NAME;

import collaborate.api.datasource.DatasourceService;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.passport.TokenMetadataProperties;
import collaborate.api.passport.model.metadata.AssetDataCatalog;
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

  Path buildRelativePathForAssetId(String assetId) {
    return Path.of(
        NFT_NAME,
        dateFormatterFactory
            .forPattern(tokenMetadataProperties.getAssetDataCatalogPartitionDatePattern()),
        // ms is added to prevent multiple creation on the same asset collision,
        //could occurs if a creation is made by providing an invalid asset Id already used on the same partition
        assetId + "_" + clock.millis()
    );
  }

  AssetDataCatalog create(CreateMultisigPassportDTO createMultisigPassportDTO) {
    var datasourceInIpfs = datasourceService
        .findById(createMultisigPassportDTO.getDatasourceUUID().toString())
        .orElseThrow(
            () -> new NoSuchElementException(
                createMultisigPassportDTO.getDatasourceUUID().toString()
            )
        );
    var datasourceRef = DatasourceLink.builder()
        .id(createMultisigPassportDTO.getDatasourceUUID().toString())
        .uri(IPFS_PROTOCOL_PREFIX + datasourceInIpfs.getCid())
        .assetIdForDatasource(createMultisigPassportDTO.getAssetIdForDatasource())
        .build();
    return AssetDataCatalog.builder()
        .datasources(List.of(datasourceRef))
        .build();
  }

}
