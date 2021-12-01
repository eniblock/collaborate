package collaborate.api.transaction;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionStateService {

  public static final String LAST_OFFSET = "lastOffset";
  private final TransactionStateDAO transactionStateDAO;

  public Optional<Long> findLastOffset(String smartContract) {
    return transactionStateDAO.findBySmartContractAndProperty(smartContract, LAST_OFFSET)
        .map(t -> Long.valueOf(t.getValue()));
  }

  public void saveLastOffset(String smartContract, Long offset) {
    var state = new TransactionWatcherState(smartContract, LAST_OFFSET, offset.toString());
    transactionStateDAO.save(state);
  }

}
