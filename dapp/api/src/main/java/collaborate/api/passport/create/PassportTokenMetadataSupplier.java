package collaborate.api.passport.create;

import collaborate.api.nft.model.metadata.License;
import collaborate.api.nft.model.metadata.TokenMetadata;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PassportTokenMetadataSupplier implements Supplier<TokenMetadata> {

  public static final String NFT_NAME = "DigitalPassport";
  public static final String NFT_SYMBOLE = "CDP";
  public static final int NFT_DECIMALS = 0;
  public static final String NFT_VERSION = "digital-passport.0.1";
  public static final String NFT_LICENCE = "MIT";
  public static final List<String> NFT_AUTHOR = List.of("The Blockchain Xdev team");
  public static final String NFT_HOMEPAGE = "https://www.theblockchainxdev.com/";
  public static final List<String> NFT_INTERFACES = List.of("TZIP-012", "TZIP-021");

  public TokenMetadata get() {
    return TokenMetadata.builder()
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
