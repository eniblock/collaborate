package collaborate.api.config.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api", ignoreUnknownFields = false)
public class ApiProperties {

  private String platform;
  private String idpAdminRole;
  private String organizationName;
  private String organizationPublicKeyHash;
  private String organizationPrivateKey;
  private String contractAddress;
  private String tezosApiGatewayUrl;
  private String certificatesPath;

}
