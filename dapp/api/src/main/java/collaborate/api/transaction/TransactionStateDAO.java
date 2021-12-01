package collaborate.api.transaction;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionStateDAO extends JpaRepository<TransactionWatcherState, String> {

  Optional<TransactionWatcherState> findBySmartContractAndProperty(String smartContract,
      String property);
}
