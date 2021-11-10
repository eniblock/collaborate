package collaborate.api.businessdata.access
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
public class GrantAccessWatcher implements TransactionHandler {

  private final GrantedAccessService grantedAccessService;
  private final OrganizationService organizationService;
  String organizationWallet = "";

  @PostConstruct
  public void init() {
    this.organizationWallet = organizationService.getCurrentOrganization().getAddress();
  }

  @Override
  public void handle(Transaction transaction) {
    if (isGrantAccessForCurrentOrganisation(transaction)) {
      log.info("New grantAccess with parameters={}", transaction.getParameters());
      grantedAccessService.storeJwtToken(transaction);
    }
  }

  boolean isGrantAccessForCurrentOrganisation(Transaction transaction) {
    boolean isRequestAccessTransaction = GrantAccessDAO.GRANT_ACCESS_ENTRY_POINT
        .equals(transaction.getEntrypoint());

    if (isRequestAccessTransaction) {
      var requesterAddress = transaction.getParameters().get("requester_address");
      return requesterAddress != null && organizationWallet.equals(requesterAddress.asText());
    }
    return false;
  }
}
