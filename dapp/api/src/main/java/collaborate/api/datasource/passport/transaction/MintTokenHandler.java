package collaborate.api.datasource.passport.transaction;

import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MintTokenHandler implements TransactionHandler {

  private final DigitalPassportTransactionService digitalPassportTransactionService;

  @Override
  public void handle(Transaction transaction) {
    if (transaction.getEntrypoint().equals("mint")){
      digitalPassportTransactionService.saveFa2Transaction(transaction);
    }

  }
}
