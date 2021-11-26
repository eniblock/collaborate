package collaborate.api.organization.tag;

import collaborate.api.organization.model.OrganizationRole;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Organization {

  private String legalName;
  private String address;
  private String encryptionKey;
  private OrganizationRole[] roles;

}
