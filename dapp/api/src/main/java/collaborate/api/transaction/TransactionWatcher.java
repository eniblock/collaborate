package collaborate.api.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class TransactionWatcher implements Runnable {

  private final String smartContractAddress;
  private final TransactionEventManager eventManager;
  private final ObjectMapper objectMapper;
  private final TezosApiGatewayTransactionClient tezosApiGatewayJobClient;

  private int page = 0;
  private int lastIndex = 0;

  @Override
  public void run() {
    var limit = 20;
    var transactionList = this.getTransactionPage(page * limit + lastIndex);

    if (!transactionList.isEmpty()) {
      // For each transaction int the result, call eventManager.notify(transaction)
      transactionList.forEach(eventManager::notify);

      if (transactionList.size() >= limit) {
        page++;
      } else {
        lastIndex += transactionList.size();
      }
    }
  }

  private List<Transaction> getTransactionPage(Integer offset) {
    var limit = 20;

    return tezosApiGatewayJobClient.getSmartContractTransactionList(
        smartContractAddress,
        offset,
        limit
    );
  }

}
