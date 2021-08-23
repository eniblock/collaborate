package collaborate.api.config.api;

import collaborate.api.domain.Organization;
import java.util.HashMap;
import java.util.Optional;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api", ignoreUnknownFields = false)
public class ApiProperties {

  private String platform;
  private String idpAdminRole;
  private String organizationId;
  private String organizationName;
  private String organizationPublicKeyHash;
  private String organizationPrivateKey;
  private String contractAddress;
  private HashMap<String, Organization> organizations;
  private String tezosApiGatewayUrl;
  private String certificatesPath;

  public Optional<Organization> findOrganizationWithOrganizationId(String organizationId) {
    return organizations.entrySet()
        .stream()
        .filter(entry -> entry.getValue().getId().equalsIgnoreCase(organizationId))
        .map(entry -> organizations.get(entry.getKey()))
        .findFirst();
  }
}
