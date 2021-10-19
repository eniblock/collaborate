package collaborate.api.datasource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "datasource", ignoreUnknownFields = false)
public class DatasourceProperties {

  @Schema(description = "The datasource root folder absolute path")
  private String rootFolder;

  @Schema(description = "The data pattern to use when storing a datasource on FS. Used to avoid max number of files in a directory")
  private String partitionDatePattern;

}
