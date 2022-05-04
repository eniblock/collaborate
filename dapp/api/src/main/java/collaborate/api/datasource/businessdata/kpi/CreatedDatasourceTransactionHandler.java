package collaborate.api.datasource.businessdata.kpi;


import static collaborate.api.datasource.businessdata.create.CreateBusinessDataNftDAO.CREATE_DATASOURCE_ENTRYPOINT;

import collaborate.api.datasource.businessdata.transaction.BusinessDataTransactionService;
import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatedDatasourceTransactionHandler implements TransactionHandler {

  private final BusinessDataKpiService businessDataKpiService;
  private final BusinessDataTransactionService businessDataTransactionService;

  @Override
  public void handle(Transaction transaction) {
    if (isCreateBusinessDatasource(transaction)) {
      businessDataKpiService.onDatasourceCreated(transaction);
      businessDataTransactionService.saveBusinessDataTransaction(transaction);
    }
  }

  boolean isCreateBusinessDatasource(Transaction transaction) {
    return CREATE_DATASOURCE_ENTRYPOINT
        .equals(transaction.getEntrypoint());
  }
}
