package collaborate.api.config.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api", ignoreUnknownFields = false)
public class ApiProperties {

  @Schema(description = "The organization name")
  private String platform;

  @Schema(description = "The \"service identity provider administrator\" role")
  private String idpAdminRole;

  @Schema(description = "The digital passport smart-contract address")
  private String digitalPassportContractAddress;

  @Schema(description = "The business data smart-contract address")
  private String businessDataContractAddress;

}
