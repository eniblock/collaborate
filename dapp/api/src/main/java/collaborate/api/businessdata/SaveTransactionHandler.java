package collaborate.api.businessdata;

import collaborate.api.businessdata.model.TransactionEntity;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SaveTransactionHandler implements TransactionHandler {

  private final TransactionDAO transactionDao;
  private final ModelMapper modelMapper;

  @Override
  public void handle(collaborate.api.transaction.Transaction transaction) {
    transactionDao.save(modelMapper.map(transaction, TransactionEntity.class));
  }
}
