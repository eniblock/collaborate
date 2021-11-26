package collaborate.api.datasource.nft.catalog.create;

import static collaborate.api.ipfs.IpfsService.IPFS_PROTOCOL_PREFIX;

import collaborate.api.datasource.nft.TokenMetadataProperties;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.ipfs.IpfsDAO;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Tzip21MetadataService {

  private final AssetDataCatalogFactory assetDataCatalogFactory;
  private final Clock clock;
  private final DateFormatterFactory dateFormatterFactory;
  private final IpfsDAO ipfsDAO;
  private final Tzip21MetadataFactory tzip21MetadataFactory;
  private final TokenMetadataProperties tokenMetadataProperties;

  public String saveMetadata(AssetDTO assetDTO, Supplier<TZip21Metadata> tokenMetadataSupplier)
      throws IOException {
    var assetDataCatalogRelativePath = assetDataCatalogFactory
        .buildRelativePathForAssetId(assetDTO)
        .toString();

    saveAssetDataCatalog(assetDTO, assetDataCatalogRelativePath);

    return IPFS_PROTOCOL_PREFIX + ipfsDAO.add(
        buildPathForAssetId(assetDTO),
        tzip21MetadataFactory.create(tokenMetadataSupplier, assetDTO, assetDataCatalogRelativePath)
    );
  }

  Path buildPathForAssetId(AssetDTO assetDTO) {
    return Path.of(
        tokenMetadataProperties.getNftMetadataRootFolder(),
        assetDTO.getAssetType(),
        dateFormatterFactory
            .forPattern(tokenMetadataProperties.getNftMetadataPartitionDatePattern()),
        assetDTO.getAssetId() + "_" + clock.millis()
    );
  }

  private void saveAssetDataCatalog(AssetDTO assetDTO,
      String assetDataCatalogRelativePath) throws IOException {
    var assetDataCatalogPath = Path.of(
        tokenMetadataProperties.getAssetDataCatalogRootFolder(),
        assetDataCatalogRelativePath
    );
    ipfsDAO.add(
        assetDataCatalogPath,
        assetDataCatalogFactory.create(assetDTO)
    );
  }

}
