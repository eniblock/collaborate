package collaborate.api.transaction;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class TransactionWatcher implements Runnable {

  private final String smartContractAddress;
  private final TransactionEventManager eventManager;

  @Override
  public void run() {
    // TODO Call TAG to get last transactions on the given smartContractAddress defined in attribute
    // For each transaction int the result, call eventManager.notify(transaction)
    log.info("hello");
  }
}
