package collaborate.api.datasource.businessdata.access
    ;

import collaborate.api.organization.OrganizationService;
import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessRequestWatcher implements TransactionHandler {

  private final AccessGrantService accessGrantService;
  private final OrganizationService organizationService;
  String organizationWallet = "";

  @PostConstruct
  public void init() {
    this.organizationWallet = organizationService.getCurrentOrganization().getAddress();
  }

  @Override
  public void handle(Transaction transaction) {
    if (isRequestAccessForCurrentOrganisation(transaction)) {
      log.info("New accessRequest with parameters={}", transaction.getParameters());
      accessGrantService.grant(transaction);
    }
  }

  boolean isRequestAccessForCurrentOrganisation(Transaction transaction) {
    boolean isRequestAccessTransaction = AccessRequestDAO.REQUEST_ACCESS_ENTRY_POINT
        .equals(transaction.getEntrypoint());

    if (isRequestAccessTransaction) {
      var providerAddress = transaction.getParameters().get("provider_address");
      return providerAddress != null && organizationWallet.equals(providerAddress.asText());
    }
    return false;
  }
}
