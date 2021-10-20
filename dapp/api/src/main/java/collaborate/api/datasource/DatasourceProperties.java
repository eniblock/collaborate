package collaborate.api.datasource;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "datasource", ignoreUnknownFields = false)
@Validated
public class DatasourceProperties {

  @Schema(description = "The datasource root folder absolute path")
  @NotEmpty
  private String rootFolder;

  @Schema(description = "The data pattern to use when storing a datasource on FS. Used to avoid max number of files in a directory")
  @NotEmpty
  private String partitionDatePattern;

}
