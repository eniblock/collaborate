package collaborate.api.datasource.servicedata.transaction;

import collaborate.api.datasource.servicedata.model.transaction.ServiceDataTransaction;
import collaborate.api.datasource.servicedata.model.transaction.ServiceDataTransactionPK;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceDataTransactionDAO extends
    JpaRepository<ServiceDataTransaction, ServiceDataTransactionPK> {

  Optional<ServiceDataTransaction> findBySmartContractAndTokenId(String smartContract,
      String tokenId);

}
