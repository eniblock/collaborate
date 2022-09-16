package collaborate.api.transaction;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class TransactionWatcher implements Runnable {

  private final String smartContractAddress;
  private final TransactionEventManager eventManager;
  private final TezosApiGatewayTransactionClient tezosApiGatewayJobClient;
  private final TransactionStateService transactionStateService;

  private long lastOffset = 0;
  private int pageSize = 20;

  @Override
  public void run() {
    if (lastOffset == 0) {
      transactionStateService.findLastOffset(smartContractAddress)
          .ifPresent(o -> lastOffset = o);
    }

    var transactionList = this.getTransactionPage(lastOffset);

    if (!transactionList.isEmpty()) {
      transactionList.forEach(eventManager::notify);
      lastOffset += transactionList.size();
      transactionStateService.saveLastOffset(smartContractAddress, lastOffset);
    }
  }

  private List<Transaction> getTransactionPage(Long offset) {
    try {
      // TODO: fix with proper log level
      return tezosApiGatewayJobClient.getSmartContractTransactionList(
        smartContractAddress,
        offset,
        pageSize
      );
    } catch(Exception e) {
      return new ArrayList<>();
    }
  }

}
