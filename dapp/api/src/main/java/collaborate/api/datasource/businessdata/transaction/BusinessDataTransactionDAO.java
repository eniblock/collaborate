package collaborate.api.datasource.businessdata.transaction;

import collaborate.api.datasource.businessdata.model.transaction.BusinessDataTransaction;
import collaborate.api.datasource.businessdata.model.transaction.BusinessDataTransactionPK;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessDataTransactionDAO extends
    JpaRepository<BusinessDataTransaction, BusinessDataTransactionPK> {

  Optional<BusinessDataTransaction> findBySmartContractAndTokenId(String smartContract,
      String tokenId);

}
