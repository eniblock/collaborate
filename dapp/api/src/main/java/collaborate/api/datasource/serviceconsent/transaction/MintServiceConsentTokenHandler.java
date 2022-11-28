package collaborate.api.datasource.serviceconsent.transaction;

import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MintServiceConsentTokenHandler implements TransactionHandler {

  private final ServiceConsentTransactionService serviceConsentTransactionService;

  @Override
  public void handle(Transaction transaction) {
    if (transaction.getEntrypoint().equals("mint")) {
      log.debug("Mint consent");
      serviceConsentTransactionService.saveFa2Transaction(transaction);
      serviceConsentTransactionService.saveNftCreated(transaction);
    }

  }
}
