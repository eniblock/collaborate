package collaborate.api.datasource.servicedata.consent;

import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogServiceDataConsentHandler implements TransactionHandler {

  @Override
  public void handle(Transaction transaction) {
    if (transaction.getEntrypoint().equals("servicedata_consent")) {
      log.info("Entrypoint = {}, params = {}", transaction.getEntrypoint(),
          transaction.getParameters());
    }
  }
}
