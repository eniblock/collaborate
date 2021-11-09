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
    listeners.add(handler);
  }

  public void unsubscribe(TransactionHandler handler) {
    listeners.remove(handler);
  }

  void notify(Transaction transaction) {
    listeners.forEach(handler -> {
      try {
        handler.handle(transaction);
      } catch (Exception e) {
        log.error(
            "Error with transaction handler={} and transaction={}, exception={}",
            handler.getClass().getName(),
            transaction,
            e
        );
      }
    });
  }

}
