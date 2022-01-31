package collaborate.api.datasource.multisig;

import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildMultiSigHandler implements TransactionHandler {

  private final ProxyTokenControllerTransactionService proxyTokenControllerTransactionService;

  @Override
  public void handle(Transaction transaction) {
    if (transaction.getEntrypoint().equals("build")) {
      proxyTokenControllerTransactionService.saveTransaction(transaction);
    }

    if (transaction.getEntrypoint().equals("sign")) {
      proxyTokenControllerTransactionService.updateTransaction(transaction);
    }

  }
}
