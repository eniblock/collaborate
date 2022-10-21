package collaborate.api.datasource.servicedata;


import static collaborate.api.datasource.servicedata.create.CreateServiceDataNftDAO.CREATE_DATASOURCE_ENTRYPOINT;

import collaborate.api.datasource.servicedata.kpi.ServiceDataKpiService;
import collaborate.api.organization.OrganizationService;
import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatedServiceScopeTransactionHandler implements TransactionHandler {

  private final ServiceDataKpiService serviceDataKpiService;
  private final OrganizationService organizationService;
  private final ServiceDataNftService nftService;

  @Override
  public void handle(Transaction transaction) {
    if (isCreateServiceDatasource(transaction)) {
      serviceDataKpiService.onScopeCreated(transaction);
      nftService.updateNft(transaction);
    }
  }

  boolean isCreateServiceDatasource(Transaction transaction) {
    return transaction.isEntryPoint(CREATE_DATASOURCE_ENTRYPOINT);
  }

}
