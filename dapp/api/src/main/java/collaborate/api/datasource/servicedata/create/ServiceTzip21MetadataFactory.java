package collaborate.api.datasource.servicedata.create;

import collaborate.api.datasource.nft.TokenMetadataProperties;
import collaborate.api.datasource.nft.catalog.create.AssetDTO;
import collaborate.api.datasource.nft.model.metadata.Attribute;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata.AttributeKeys;
import collaborate.api.ipfs.IpfsService;
import collaborate.api.ipfs.IpnsService;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ServiceTzip21MetadataFactory {

  private final IpnsService ipnsService;
  private final TokenMetadataProperties tokenMetadataProperties;

  public TZip21Metadata create(AssetDTO assetDTO) {
    var catalogAttribute = Attribute.builder()
        .name(AttributeKeys.ASSET_DATA_CATALOG)
        .value(buildAssetDataCatalogIpnsPath(assetDTO.getAssetRelativePath()))
        .type("URI")
        .build();

    return assetDTO.getTZip21Metadata().toBuilder()
        .attributes(List.of(catalogAttribute))
        .build();
  }


  String buildAssetDataCatalogIpnsPath(String assetDataCatalogRelativePath) {
    var ipnsRoot = ipnsService
        .getKeyPairByName(tokenMetadataProperties.getAssetDataCatalogRootFolder())
        .orElseThrow(() -> new NoSuchElementException(
            tokenMetadataProperties.getAssetDataCatalogRootFolder())
        ).getId();

    return IpfsService.IPNS_PROTOCOL_PREFIX + Path.of(ipnsRoot, assetDataCatalogRelativePath);
  }


}
