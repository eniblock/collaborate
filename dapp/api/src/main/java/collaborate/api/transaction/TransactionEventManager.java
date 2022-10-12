package collaborate.api.transaction;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class TransactionEventManager {

  private final List<TransactionHandler> listeners = new ArrayList<>();

  public void subscribe(TransactionHandler handler) {
    log.info("Adding transactionHandler={}", handler.getClass());
    listeners.add(handler);
  }

  void notify(Transaction transaction) {
    log.info("Dispatching transaction.hash={}", transaction.getHash());
    listeners.forEach(handler -> {
      try {
        handler.handle(transaction);
      } catch (Exception e) {
        log.error(
            "Error with transaction handler={} and transaction={}",
            handler.getClass().getName(),
            transaction
        );
        log.error("Exception", e);
      }
    });
  }

}
