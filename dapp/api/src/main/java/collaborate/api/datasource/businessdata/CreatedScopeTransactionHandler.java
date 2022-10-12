package collaborate.api.datasource.businessdata;


import static collaborate.api.datasource.businessdata.create.CreateBusinessDataNftDAO.CREATE_DATASOURCE_ENTRYPOINT;

import collaborate.api.datasource.businessdata.kpi.BusinessDataKpiService;
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
  private final NftService nftService;

  @Override
  public void handle(Transaction transaction) {
    if (isCreateBusinessDatasource(transaction)) {
      businessDataKpiService.onScopeCreated(transaction);
      nftService.updateNft(transaction);
    }
  }

  boolean isCreateBusinessDatasource(Transaction transaction) {
    return transaction.isEntryPoint(CREATE_DATASOURCE_ENTRYPOINT);
  }

}
