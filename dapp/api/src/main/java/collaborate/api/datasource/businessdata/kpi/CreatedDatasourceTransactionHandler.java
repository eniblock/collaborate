package collaborate.api.datasource.businessdata.kpi;


import static collaborate.api.datasource.businessdata.create.CreateBusinessDataNftDAO.DATA_CATALOG_CREATION_ENTRYPOINT;

import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatedDatasourceTransactionHandler implements TransactionHandler {

  private final BusinessDataKpiService businessDataKpiService;

  @Override
  public void handle(Transaction transaction) {
    if (isCreateBusinessDatasource(transaction)) {
      businessDataKpiService.onDatasourceCreated(transaction);
    }
  }

  boolean isCreateBusinessDatasource(Transaction transaction) {
    return DATA_CATALOG_CREATION_ENTRYPOINT
        .equals(transaction.getEntrypoint());
  }
}
