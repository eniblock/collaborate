package collaborate.api.config.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "keycloak-admin-client-properties", ignoreUnknownFields = false)
public class KeycloakAdminClientProperties {

  private String baseUrl;
  private String realm;
  private String user;
  private String password;
  private String clientId;
  private String clientSecret;
  private String grantType;
  private boolean verifyHostname;

}
