package collaborate.api.passport.consent;

import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogPassportConsentHandler implements TransactionHandler {

  @Override
  public void handle(Transaction transaction) {
    if (transaction.getEntrypoint().equals("passport_consent")) {
      log.info("Entrypoint = {}, params = {}", transaction.getEntrypoint(),
          transaction.getParameters());
    }
  }
}
