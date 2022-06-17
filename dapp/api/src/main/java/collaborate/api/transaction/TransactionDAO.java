package collaborate.api.transaction;

import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionDAO extends
    JpaRepository<Transaction, String> {

  Page<Transaction> findByDestinationIn(Collection<String> receivers, Pageable pageable);

  Page<Transaction> findByDestinationInAndSource(Collection<String> receivers, String source,
      Pageable pageable);
}
