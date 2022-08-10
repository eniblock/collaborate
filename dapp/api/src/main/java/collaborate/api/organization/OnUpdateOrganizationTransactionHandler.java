package collaborate.api.organization;

import collaborate.api.organization.model.UpdateOrganizationTypeDTO;
import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnUpdateOrganizationTransactionHandler implements TransactionHandler {

  private static final String UPDATE_ORGANIZATIONS = "update_organizations";
  private final ObjectMapper objectMapper;
  private final OrganizationService organizationService;
  private final PendingOrganizationService pendingOrganizationService;

  @Override
  public void handle(Transaction transaction) {
    if (transaction.isEntryPoint(UPDATE_ORGANIZATIONS)) {
      log.info("New organization update, parameters={}", transaction.getParameters());
      var updatesOrRemoveOrgs = toUpdateTransactionTypeDTOs(transaction.getParameters());

      if (transaction.isSender(organizationService.getCurrentOrganization().getAddress())) {
        pendingOrganizationService.activatePendingWallets(updatesOrRemoveOrgs);
      }
      pendingOrganizationService.removePendings(updatesOrRemoveOrgs);
    }
    organizationService.clearCache();
  }

  List<UpdateOrganizationTypeDTO> toUpdateTransactionTypeDTOs(JsonNode transactionParameters) {
    try {
      ObjectReader reader = objectMapper.readerFor(
          new TypeReference<List<UpdateOrganizationTypeDTO>>() {
          });
      return reader.readValue(transactionParameters);
    } catch (IOException e) {
      log.error(
          "While converting transactionParameters={} to UpdateOrganizationTypeDTO",
          transactionParameters);
      throw new IllegalStateException(e);
    }
  }

}
