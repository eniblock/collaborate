package collaborate.api.repository;

import collaborate.api.domain.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, UUID> {
    AccessRequest findOneByProviderAddressAndRequesterAddressAndDatasourceIdAndScopeOrderByCreatedAtDesc(String providerAddress, String requesterAddress, Long datasourceId, String scope);
}
