package collaborate.api.config.api;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "ipfs")
@Validated
public class IpfsProperties {

  @NotEmpty
  private String url;
}
