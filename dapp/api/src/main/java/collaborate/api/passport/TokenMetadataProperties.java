package collaborate.api.passport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "token-metadata", ignoreUnknownFields = false)
public class TokenMetadataProperties {

  @Schema(description = "The nft metadata root folder absolute path")
  private String nftMetadataRootFolder;

  @Schema(description = "The data pattern to use when storing a nft metadata on FS. Used to avoid max number of files in a directory")
  private String nftMetadataPartitionDatePattern;

  @Schema(description = "The catalog metadata root folder absolute path")
  private String assetDataCatalogRootFolder;

  @Schema(description = "The data pattern to use when storing a catalog metadata on FS. Used to avoid max number of files in a directory")
  private String assetDataCatalogPartitionDatePattern;

}
