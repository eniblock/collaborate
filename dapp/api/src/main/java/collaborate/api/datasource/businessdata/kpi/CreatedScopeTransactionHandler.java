package collaborate.api.datasource.businessdata.kpi;


import static collaborate.api.datasource.businessdata.create.CreateBusinessDataNftDAO.CREATE_DATASOURCE_ENTRYPOINT;

import collaborate.api.datasource.businessdata.document.AssetsService;
import collaborate.api.organization.OrganizationService;
import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatedScopeTransactionHandler implements TransactionHandler {

  private final BusinessDataKpiService businessDataKpiService;
  private final OrganizationService organizationService;
  private final AssetsService assetsService;

  @Override
  public void handle(Transaction transaction) {
    if (isCreateBusinessDatasource(transaction)) {
      businessDataKpiService.onScopeCreated(transaction);
      handleUpdateAssetScopeNftId(transaction);
    }
  }

  boolean isCreateBusinessDatasource(Transaction transaction) {
    return transaction.isEntryPoint(CREATE_DATASOURCE_ENTRYPOINT);
  }

  void handleUpdateAssetScopeNftId(Transaction transaction) {
    String currentOrganizationAddress = organizationService.getCurrentOrganization().getAddress();
    if (transaction.isSender(currentOrganizationAddress)) {
      assetsService.updateNftId(transaction, currentOrganizationAddress);
    }
  }
}
