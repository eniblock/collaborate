package collaborate.api.config.api;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "smart-contract-address", ignoreUnknownFields = false)
@Validated
public class SmartContractAddressProperties {

  @Schema(description = "The business data smart-contract address")
  private String businessData;

  @Schema(description = "The digital passport smart-contract address")
  private String digitalPassport;

  @Schema(description = "The digital passport Proxy Token Controller smart-contract address")
  private String digitalPassportProxyTokenController;

  @Schema(description = "The organization yellow page smart-contract address")
  @NotEmpty
  private String organizationYellowPage;
}
