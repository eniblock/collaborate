package collaborate.api.datasource.servicedata.transaction;

import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MintServiceDataTokenHandler implements TransactionHandler {

  private final ServiceDataTransactionService serviceDataTransactionService;

  @Override
  public void handle(Transaction transaction) {
    if (transaction.getEntrypoint().equals("mint")) {
      serviceDataTransactionService.saveFa2Transaction(transaction);
      serviceDataTransactionService.saveNftCreated(transaction);
    }

  }
}
