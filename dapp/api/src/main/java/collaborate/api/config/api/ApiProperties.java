package collaborate.api.config.api;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "api", ignoreUnknownFields = false)
@Validated
public class ApiProperties {

  @Schema(description = "The \"service identity provider administrator\" role")
  @NotEmpty
  private String idpAdminRole;

  @Schema(description = "The organization name")
  @NotEmpty
  private String platform;

  @Schema(description = "The organization private encryption key ")
  private String privateKey;

  @Schema(description = "The organization public encryption key ")
  private String publicEncryptionKey;

}
