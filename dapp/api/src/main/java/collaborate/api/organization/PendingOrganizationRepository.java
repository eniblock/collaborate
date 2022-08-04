package collaborate.api.organization;

import collaborate.api.organization.tag.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingOrganizationRepository extends
    JpaRepository<Organization, String> {

}
