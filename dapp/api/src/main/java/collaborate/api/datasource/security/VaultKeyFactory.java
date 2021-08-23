package collaborate.api.datasource.security;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class VaultKeyFactory {

  public String createBasicAuth(UUID datasourceUUID) {
    return "datasource/" + datasourceUUID + "/authentication/basic-auth";
  }

  public String createOAuth2(UUID datasourceUUID) {
    return "datasource/" + datasourceUUID + "/authentication/o-auth-2";
  }
}
