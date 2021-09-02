package collaborate.api.config.api;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Transient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api", ignoreUnknownFields = false)
public class ApiProperties {

  @Schema(description = "The organization name")
  private String platform;

  @Schema(description = "The \"service identity provider administrator\" role")
  private String idpAdminRole;

  @Schema(description = "The full organization name")
  private String organizationName;

  @Schema(description = "The organization public hash key")
  private String organizationPublicKeyHash;

  @Transient
  private String organizationPrivateKey;

  @Schema(description = "The smart-contract address")
  private String contractAddress;

  @Schema(description = "The Traefik configuration")
  private TraefikProperties traefik;

}
