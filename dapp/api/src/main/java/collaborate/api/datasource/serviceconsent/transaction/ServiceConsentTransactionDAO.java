package collaborate.api.datasource.serviceconsent.transaction;

import collaborate.api.datasource.serviceconsent.model.transaction.Fa2ServiceConsentTransaction;
import collaborate.api.datasource.serviceconsent.model.transaction.Fa2ServiceConsentTransactionPK;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceConsentTransactionDAO extends JpaRepository<Fa2ServiceConsentTransaction, Fa2ServiceConsentTransactionPK> {

  Optional<Fa2ServiceConsentTransaction> findBySmartContractAndTokenId(String smartContract, Long tokenId);

}
