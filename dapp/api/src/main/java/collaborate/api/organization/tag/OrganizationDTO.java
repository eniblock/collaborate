package collaborate.api.organization.tag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDTO {

  private String name;
  private String publicKey;
  private String publicKeyHash;

}
