package collaborate.api.passport.create;

import collaborate.api.date.DateFormatterFactory;
import collaborate.api.ipfs.IpfsService;
import collaborate.api.ipfs.IpnsService;
import collaborate.api.passport.TokenMetadataProperties;
import collaborate.api.passport.model.metadata.Attribute;
import collaborate.api.passport.model.metadata.License;
import collaborate.api.passport.model.metadata.TokenMetadata;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenMetadataFactory {

  public final static String NFT_NAME = "DigitalPassport";
  public final static String NFT_SYMBOLE = "CDP";
  public final static int NFT_DECIMALS = 0;
  public final static String NFT_VERSION = "digital-passport.0.1";
  public final static String NFT_LICENCE = "MIT";
  public final static List<String> NFT_AUTHOR = List.of("The Blockchain Xdev team");
  public final static String NFT_HOMEPAGE = "https://www.theblockchainxdev.com/";
  public final static List<String> NFT_INTERFACES = List.of("TZIP-012", "TZIP-021");

  private final Clock clock;
  private final DateFormatterFactory dateFormatterFactory;
  private final IpnsService ipnsService;
  private final TokenMetadataProperties tokenMetadataProperties;

  public Path buildPathForAssetId(String assetId) {
    return Path.of(
        tokenMetadataProperties.getNftMetadataRootFolder(),
        NFT_NAME,
        dateFormatterFactory
            .forPattern(tokenMetadataProperties.getNftMetadataPartitionDatePattern()),
        assetId + "_" + clock.millis()
    );
  }

  public TokenMetadata create(CreateMultisigPassportDTO createMultisigPassportDTO,
      String assetDataCatalogRelativePath) {
    var attribute = Attribute.builder()
        .name("assetDataCatalog")
        .value(IpfsService.IPNS_PROTOCOL_PREFIX
            + buildAssetDataCatalogIpnsPath(assetDataCatalogRelativePath)
        ).type("URI")
        .build();

    return TokenMetadata.builder()
        .name(NFT_NAME)
        .symbol(NFT_SYMBOLE)
        .decimals(NFT_DECIMALS)
        .description(
            "The metadata for the digital-passport asset having '"
                + createMultisigPassportDTO.getAssetId()
                + "' assetId")
        .version(NFT_VERSION)
        .license(License.builder().name(NFT_LICENCE).build())
        .authors(NFT_AUTHOR)
        .homepage(NFT_HOMEPAGE)
        .interfaces(NFT_INTERFACES)
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
