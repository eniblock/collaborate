package collaborate.api.organization.tag;

import collaborate.api.organization.model.OrganizationDTO;
import org.springframework.stereotype.Component;

@Component
public class UpdateOrganisationFactory {

  public UpdateOrganizationType update(OrganizationDTO organizationDTO) {
    var organization = Organization.builder()
        .address(organizationDTO.getAddress())
        .roles(organizationDTO.getRoles())
        .legalName(organizationDTO.getLegalName())
        .encryptionKey(organizationDTO.getEncryptionKey())
        .build();
    return new UpdateOrganizationType(organization, null);
  }

  public UpdateOrganizationType remove(String address) {
    return new UpdateOrganizationType(null, address);
  }
}
