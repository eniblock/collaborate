package collaborate.api.config.api;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Configuration
@ConfigurationProperties(prefix = "traefik", ignoreUnknownFields = false)
@Validated
public class TraefikProperties {

  @Schema(description = "The path to store Traefik datasource certificates")
  @NotEmpty
  private String certificatesPath;

  @Schema(description = "The path to store Traefik datasource provider configuration")
  @NotEmpty
  private String providersPath;

  @Schema(description = "The path where is stored the script used to remove a passphrase from a pfx file")
  @NotEmpty
  private String pfxUnProtectorScriptPath;

  @Schema(description = "The url to use for requesting the inner API gateway used for consuming datasource")
  @NotEmpty
  private String url;
}
