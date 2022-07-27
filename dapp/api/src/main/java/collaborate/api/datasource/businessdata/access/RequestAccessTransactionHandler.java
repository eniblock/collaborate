package collaborate.api.datasource.businessdata.access;

import static collaborate.api.datasource.businessdata.access.RequestAccessDAO.REQUEST_ACCESS_ENTRY_POINT;
import static collaborate.api.datasource.businessdata.access.model.AccessRequestParams.AttributeName.PROVIDER_ADDRESS;

import collaborate.api.organization.OrganizationService;
import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * WHEN a Block chain transaction {@link #isRequestAccessForCurrentOrganisation(Transaction)} <br>
 * THEN Call the {@link GrantAccessService#grant(Transaction)}
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RequestAccessTransactionHandler implements TransactionHandler {

  private final GrantAccessService accessGrantService;
  private final OrganizationService organizationService;

  @Override
  public void handle(Transaction transaction) {
    if (isRequestAccessForCurrentOrganisation(transaction)) {
      log.info("New accessRequest with parameters={}", transaction.getParameters());
      accessGrantService.grant(transaction);
    }
  }

  boolean isRequestAccessForCurrentOrganisation(Transaction transaction) {
    return transaction.isEntryPoint(REQUEST_ACCESS_ENTRY_POINT) &&
        transaction.hasParameterValue(
            PROVIDER_ADDRESS,
            organizationService.getCurrentOrganization().getAddress()
        );
  }
}
