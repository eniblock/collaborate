package collaborate.api.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionPersistenceHandler implements TransactionHandler {

  private final TransactionDAO transactionDAO;

  @Override
  public void handle(Transaction transaction) {
    transactionDAO.save(transaction);
  }
}
