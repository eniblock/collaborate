package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.businessdata.access.model.PendingAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingAccessRequestRepository extends
    JpaRepository<PendingAccessRequest, PendingAccessRequest.Id> {

}
