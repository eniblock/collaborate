package collaborate.api.datasource.businessdata.create;

import collaborate.api.datasource.nft.model.metadata.License;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BusinessDataTokenMetadataSupplier implements Supplier<TZip21Metadata> {

  public static final String NFT_NAME = "CatalogBusinessData";
  public static final String NFT_SYMBOLE = "CBD";
  public static final int NFT_DECIMALS = 0;
  public static final String NFT_VERSION = "business-data.0.1";
  public static final String NFT_LICENCE = "MIT";
  public static final List<String> NFT_AUTHOR = List.of("The Blockchain Xdev team");
  public static final String NFT_HOMEPAGE = "https://www.theblockchainxdev.com/";
  public static final List<String> NFT_INTERFACES = List.of("TZIP-012", "TZIP-021");

  public TZip21Metadata get() {
    return TZip21Metadata.builder()
        .name(NFT_NAME)
        .symbol(NFT_SYMBOLE)
        .decimals(NFT_DECIMALS)
        .version(NFT_VERSION)
        .license(License.builder().name(NFT_LICENCE).build())
        .authors(NFT_AUTHOR)
        .homepage(NFT_HOMEPAGE)
        .interfaces(NFT_INTERFACES)
        .build();
  }

}
