package collaborate.api.datasource.nft.catalog.create;

import collaborate.api.datasource.nft.TokenMetadataProperties;
import collaborate.api.datasource.nft.model.metadata.Attribute;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.ipfs.IpfsService;
import collaborate.api.ipfs.IpnsService;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Tzip21MetadataFactory {

  private final IpnsService ipnsService;
  private final TokenMetadataProperties tokenMetadataProperties;

  public TZip21Metadata create(
      Supplier<TZip21Metadata> tokenMetadataSupplier,
      AssetDTO assetDTO, String assetDataCatalogRelativePath) {
    var catalogAttribute = Attribute.builder()
        .name("assetDataCatalog")
        .value(IpfsService.IPNS_PROTOCOL_PREFIX
            + buildAssetDataCatalogIpnsPath(assetDataCatalogRelativePath)
        )
        .type("URI")
        .build();

    var assetIdAttribute = Attribute.builder()
        .name("assetId")
        .value(assetDTO.getAssetId())
        .type("String")
        .build();

    return tokenMetadataSupplier.get().toBuilder()
        .description(
            "The metadata for the " + assetDTO.getAssetType() + " asset having '"
                + assetDTO.getAssetId()
                + "' assetId")
        .attributes(List.of(catalogAttribute, assetIdAttribute))
        .build();
  }


  String buildAssetDataCatalogIpnsPath(String assetDataCatalogRelativePath) {
    var ipnsRoot = ipnsService
        .getKeyPairByName(tokenMetadataProperties.getAssetDataCatalogRootFolder())
        .orElseThrow(() -> new NoSuchElementException(
            tokenMetadataProperties.getAssetDataCatalogRootFolder())
        ).getId();

    return Path.of(ipnsRoot, assetDataCatalogRelativePath).toString();
  }


}
