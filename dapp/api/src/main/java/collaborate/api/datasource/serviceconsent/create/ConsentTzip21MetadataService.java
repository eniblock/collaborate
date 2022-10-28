package collaborate.api.datasource.serviceconsent.create;

import static collaborate.api.ipfs.IpfsService.IPFS_PROTOCOL_PREFIX;

import collaborate.api.datasource.nft.TokenMetadataProperties;
import collaborate.api.datasource.nft.catalog.create.AssetDTO;
import collaborate.api.datasource.nft.catalog.create.AssetDataCatalogFactory;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.ipfs.IpfsDAO;
import java.io.IOException;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsentTzip21MetadataService {

  private final AssetDataCatalogFactory assetDataCatalogFactory;
  private final DateFormatterFactory dateFormatterFactory;
  private final IpfsDAO ipfsDAO;
  private final ConsentTzip21MetadataFactory businessTzip21MetadataFactory;
  private final TokenMetadataProperties tokenMetadataProperties;

  public String saveMetadata(AssetDTO assetDTO)
      throws IOException {

    var tzip21 = businessTzip21MetadataFactory.create(assetDTO);
    saveAssetDataCatalog(assetDTO);

    return IPFS_PROTOCOL_PREFIX + ipfsDAO.add(buildPathForAssetId(assetDTO), tzip21);
  }

  Path buildPathForAssetId(AssetDTO assetDTO) {
    return Path.of(
        tokenMetadataProperties.getNftMetadataRootFolder(),
        assetDTO.getAssetType(),
        dateFormatterFactory
            .forPattern(tokenMetadataProperties.getNftMetadataPartitionDatePattern()),
        assetDTO.getAssetRelativePath()
    );
  }

  private void saveAssetDataCatalog(AssetDTO assetDTO) throws IOException {
    var assetDataCatalogPath = Path.of(
        tokenMetadataProperties.getAssetDataCatalogRootFolder(),
        assetDTO.getAssetRelativePath()
    );
    ipfsDAO.add(
        assetDataCatalogPath,
        assetDataCatalogFactory.create(assetDTO)
    );
  }

}
