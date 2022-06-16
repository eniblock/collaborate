package collaborate.api.organization;

import collaborate.api.cache.CacheConfig.CacheNames;
import collaborate.api.cache.CacheService;
import collaborate.api.datasource.businessdata.access.GrantAccessService;
import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
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

  @Override
  public void handle(Transaction transaction) {
    if (isUpdateTransaction(transaction)) {
      log.info("New organization update, parameters={}", transaction.getParameters());
      cacheService.clearOrThrow(CacheNames.ORGANIZATION);
    }
  }

  boolean isUpdateTransaction(Transaction transaction) {
    return transaction.isEntryPoint(UPDATE_ORGANIZATIONS);
  }
}
