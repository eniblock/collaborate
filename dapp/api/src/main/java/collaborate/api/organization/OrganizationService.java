package collaborate.api.organization;

import collaborate.api.organization.model.OrganizationDTO;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationService {

  private final OrganizationDAO organizationDAO;

  public Collection<OrganizationDTO> getAllOrganizations() {
    return organizationDAO.getAllOrganizations();
  }

}
