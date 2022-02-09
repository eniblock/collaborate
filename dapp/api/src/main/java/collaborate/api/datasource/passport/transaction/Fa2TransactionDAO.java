package collaborate.api.datasource.passport.transaction;

import collaborate.api.datasource.passport.model.transaction.Fa2Transaction;
import collaborate.api.datasource.passport.model.transaction.Fa2TransactionPK;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Fa2TransactionDAO extends JpaRepository<Fa2Transaction, Fa2TransactionPK> {

  Optional<Fa2Transaction> findBySmartContractAndTokenId(String smartContract, Long tokenId);

}
