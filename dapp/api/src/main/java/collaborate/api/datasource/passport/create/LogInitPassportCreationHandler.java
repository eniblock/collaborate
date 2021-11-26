package collaborate.api.datasource.passport.create;

import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogInitPassportCreationHandler implements TransactionHandler {

  @Override
  public void handle(Transaction transaction) {
    if (transaction.getEntrypoint().equals("init_passport_creation")) {
      log.info("Entrypoint = {}, params = {}", transaction.getEntrypoint(),
          transaction.getParameters());
    }
  }
}
