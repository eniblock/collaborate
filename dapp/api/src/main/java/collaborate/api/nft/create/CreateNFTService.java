package collaborate.api.nft.create;

import static collaborate.api.ipfs.IpfsService.IPFS_PROTOCOL_PREFIX;

import collaborate.api.ipfs.IpfsDAO;
import collaborate.api.nft.TokenMetadataProperties;
import collaborate.api.nft.model.metadata.TokenMetadata;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateNFTService {

  private final AssetDataCatalogFactory assetDataCatalogFactory;
  private final IpfsDAO ipfsDAO;
  private final TokenMetadataFactory tokenMetadataFactory;
  private final TokenMetadataProperties tokenMetadataProperties;

  public String saveMetadata(AssetDTO assetDTO, Supplier<TokenMetadata> tokenMetadataSupplier)
      throws IOException {
    var assetDataCatalogRelativePath = assetDataCatalogFactory
        .buildRelativePathForAssetId(assetDTO)
        .toString();

    saveAssetDataCatalog(assetDTO, assetDataCatalogRelativePath);

    return IPFS_PROTOCOL_PREFIX + ipfsDAO.add(
        tokenMetadataFactory.buildPathForAssetId(assetDTO),
        tokenMetadataFactory.create(tokenMetadataSupplier, assetDTO, assetDataCatalogRelativePath)
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
