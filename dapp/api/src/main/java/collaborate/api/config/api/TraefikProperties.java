package collaborate.api.config.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.nio.file.Path;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "api.traefik", ignoreUnknownFields = false)
public class TraefikProperties {

  @Schema(description = "The path to store Traefik datasource certificates")
  private Path certificatesPath;

  @Schema(description = "The path to store Traefik datasource provider configuration")
  private Path providersPath;
}
