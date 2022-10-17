package collaborate.api.datasource.servicedata.transaction;

import collaborate.api.datasource.servicedata.model.transaction.Fa2ServiceDataTransaction;
import collaborate.api.datasource.servicedata.model.transaction.Fa2ServiceDataTransactionPK;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceDataTransactionDAO extends JpaRepository<Fa2ServiceDataTransaction, Fa2ServiceDataTransactionPK> {

  Optional<Fa2ServiceDataTransaction> findBySmartContractAndTokenId(String smartContract, Long tokenId);

}
