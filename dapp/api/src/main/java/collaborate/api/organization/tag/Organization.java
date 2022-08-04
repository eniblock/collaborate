package collaborate.api.organization.tag;

import collaborate.api.organization.model.OrganizationRole;
import collaborate.api.organization.model.OrganizationRoleConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "pending_organization")
@ToString
public class Organization {

  private String legalName;
  @Id
  private String address;
  @Column(length=392)
  private String encryptionKey;
  @Column(name = "roles", nullable = false)
  @Convert(converter = OrganizationRoleConverter.class)
  private List<OrganizationRole> roles;

}
