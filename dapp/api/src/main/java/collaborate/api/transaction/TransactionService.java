package collaborate.api.transaction;

import collaborate.api.datasource.kpi.find.SearchCriteria;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final TransactionDAO transactionDAO;
  private final TransactionCustomDAO transactionCustomDAO;
  private final Collection<String> allSmartContracts;

  public Page<Transaction> findAllOnKnownSmartContracts(Optional<String> senderAddress,
      Pageable pageable) {
    return senderAddress.map(
        s -> transactionDAO.findByDestinationInAndSource(allSmartContracts, s, pageable)
    ).orElseGet(() -> transactionDAO.findByDestinationIn(allSmartContracts, pageable));
  }

  public Collection<Transaction> find(Collection<SearchCriteria> searchCriteria) {
    return transactionCustomDAO.find(searchCriteria);
  }
}
