package collaborate.api.nft.create;

import collaborate.api.date.DateFormatterFactory;
import collaborate.api.ipfs.IpfsService;
import collaborate.api.ipfs.IpnsService;
import collaborate.api.nft.TokenMetadataProperties;
import collaborate.api.nft.model.metadata.Attribute;
import collaborate.api.nft.model.metadata.TokenMetadata;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenMetadataFactory {

  private final Clock clock;
  private final DateFormatterFactory dateFormatterFactory;
  private final IpnsService ipnsService;
  private final TokenMetadataProperties tokenMetadataProperties;

  public Path buildPathForAssetId(AssetDTO assetDTO) {
    return Path.of(
        tokenMetadataProperties.getNftMetadataRootFolder(),
        assetDTO.getAssetType(),
        dateFormatterFactory
            .forPattern(tokenMetadataProperties.getNftMetadataPartitionDatePattern()),
        assetDTO.getAssetId() + "_" + clock.millis()
    );
  }

  public TokenMetadata create(
      Supplier<TokenMetadata> tokenMetadataSupplier,
      AssetDTO assetDTO, String assetDataCatalogRelativePath) {
    var attribute = Attribute.builder()
        .name("assetDataCatalog")
        .value(IpfsService.IPNS_PROTOCOL_PREFIX
            + buildAssetDataCatalogIpnsPath(assetDataCatalogRelativePath)
        ).type("URI")
        .build();

    return tokenMetadataSupplier.get().toBuilder()
        .description(
            "The metadata for the " + assetDTO.getAssetType() + " asset having '"
                + assetDTO.getAssetId()
                + "' assetId")
        .attributes(List.of(attribute))
        .build();
  }


  public String buildAssetDataCatalogIpnsPath(String assetDataCatalogRelativePath) {
    // TODO ensure to use an existing assetDataCatalogRelativePath path
    var ipnsRoot = ipnsService
        .getKeyPairByName(tokenMetadataProperties.getAssetDataCatalogRootFolder())
        .orElseThrow(() -> new NoSuchElementException(
            tokenMetadataProperties.getAssetDataCatalogRootFolder())
        ).getId();

    return Path.of(ipnsRoot, assetDataCatalogRelativePath).toString();
  }


}
