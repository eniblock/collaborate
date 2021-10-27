package collaborate.api.transaction;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TransactionEventManager {

  private List<TransactionHandler> listeners;

  void subscribe(TransactionHandler handler) {
    // TODO
  }

  void unsubscribe(TransactionHandler handler) {
    // TODO
  }

  void notify(Transaction transaction) {
    listeners.forEach(t -> t.handle(transaction));
  }

}
