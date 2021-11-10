package collaborate.api.transaction;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TransactionEventManager {

  private List<TransactionHandler> listeners = new ArrayList<>();

  public void subscribe(TransactionHandler handler) {
    listeners.add(handler);
  }

  public void unsubscribe(TransactionHandler handler) {
    listeners.remove(handler);
  }

  void notify(Transaction transaction) {
    listeners.forEach(t -> t.handle(transaction));
  }

}
