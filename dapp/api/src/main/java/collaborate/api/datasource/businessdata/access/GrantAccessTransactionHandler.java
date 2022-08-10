package collaborate.api.datasource.businessdata.access
    ;

import collaborate.api.datasource.businessdata.kpi.BusinessDataKpiService;
import collaborate.api.organization.OrganizationService;
import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GrantAccessTransactionHandler implements TransactionHandler {

  private final BusinessDataKpiService businessDataKpiService;
  private final GrantedAccessService grantedAccessService;
  private final OrganizationService organizationService;

  @Override
  public void handle(Transaction transaction) {
    if (isGrantAccessForCurrentOrganisation(transaction)) {
      log.info("New grantAccess with parameters={}", transaction.getParameters());
      grantedAccessService.onGrantedAccess(transaction);
    }
    if (isGrantAccess(transaction)) {
      businessDataKpiService.onGrantedAccess(transaction);
    }
  }

  boolean isGrantAccess(Transaction transaction) {
    return GrantAccessDAO.GRANT_ACCESS_ENTRY_POINT.equals(transaction.getEntrypoint());
  }

  boolean isGrantAccessForCurrentOrganisation(Transaction transaction) {
    if (isGrantAccess(transaction)) {
      var requesterAddress = transaction.getParameters().get("requester_address");
      return requesterAddress != null &&
          organizationService.getCurrentOrganization()
              .getAddress()
              .equals(requesterAddress.asText());
    }
    return false;
  }
}
