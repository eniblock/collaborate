package collaborate.api.organization;

import collaborate.api.cache.CacheConfig.CacheNames;
import collaborate.api.cache.CacheService;
import collaborate.api.datasource.businessdata.access.GrantAccessService;
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

/**
 * WHEN a Block chain transaction {@link #isUpdateTransaction(Transaction)} <br> THEN Call the
 * {@link GrantAccessService#grant(Transaction)}
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OnUpdateOrganizationTransactionHandler implements TransactionHandler {

  private static final String UPDATE_ORGANIZATIONS = "update_organizations";
  private final CacheService cacheService;
  private final OrganizationService organizationService;
  private final ObjectMapper objectMapper;

  @Override
  public void handle(Transaction transaction) {
    if (isUpdateTransaction(transaction)) {
      log.info("New organization update, parameters={}", transaction.getParameters());
      toUpdateTransactionTypeDTOs(transaction.getParameters())
          .forEach(update -> organizationService.removePending(update.getAddress()));
      cacheService.clearOrThrow(CacheNames.ORGANIZATION);
    }
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

  boolean isUpdateTransaction(Transaction transaction) {
    return transaction.isEntryPoint(UPDATE_ORGANIZATIONS);
  }
}
