package collaborate.api.transaction;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class TransactionWatcher implements Runnable {

  private final String smartContractAddress;
  private final TransactionEventManager eventManager;
  private final ObjectMapper objectMapper;

  @Override
  public void run() {
    // TODO Call TAG to get last transactions on the given smartContractAddress defined in attribute
    // For each transaction int the result, call eventManager.notify(transaction)
    var transactionParameters = "jsonString";
    try {
      var jsonNode = objectMapper.readTree(transactionParameters);
    } catch (JsonProcessingException e) {
      log.error("While parsing transaction parameters={}, exception={}", transactionParameters, e);
    }
    log.info("hello");
  }
}
