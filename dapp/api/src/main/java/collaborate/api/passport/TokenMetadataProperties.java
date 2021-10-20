package collaborate.api.passport;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "token-metadata", ignoreUnknownFields = false)
@Validated
public class TokenMetadataProperties {

  @Schema(description = "The nft metadata root folder absolute path")
  @NotEmpty
  private String nftMetadataRootFolder;

  @Schema(description = "The data pattern to use when storing a nft metadata on FS. Used to avoid max number of files in a directory")
  @NotEmpty
  private String nftMetadataPartitionDatePattern;

  @Schema(description = "The catalog metadata root folder absolute path")
  @NotEmpty
  private String assetDataCatalogRootFolder;

  @Schema(description = "The data pattern to use when storing a catalog metadata on FS. Used to avoid max number of files in a directory")
  @NotEmpty
  private String assetDataCatalogPartitionDatePattern;

}
