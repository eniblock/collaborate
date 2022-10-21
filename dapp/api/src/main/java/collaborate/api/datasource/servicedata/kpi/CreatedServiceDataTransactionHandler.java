package collaborate.api.datasource.servicedata.kpi;


import static collaborate.api.datasource.servicedata.create.CreateServiceDataNftDAO.CREATE_DATASOURCE_ENTRYPOINT;

import collaborate.api.datasource.servicedata.transaction.ServiceDataTransactionService;
import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatedServiceDataTransactionHandler implements TransactionHandler {

  private final ServiceDataKpiService serviceDataKpiService;
  private final ServiceDataTransactionService serviceDataTransactionService;

  @Override
  public void handle(Transaction transaction) {
    if (isCreateServiceDatasource(transaction)) {
      serviceDataKpiService.onDatasourceCreated(transaction);
      serviceDataTransactionService.saveServiceDataTransaction(transaction);
    }
  }

  boolean isCreateServiceDatasource(Transaction transaction) {
    return CREATE_DATASOURCE_ENTRYPOINT
        .equals(transaction.getEntrypoint());
  }
}
