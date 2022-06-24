package collaborate.api.datasource.businessdata;

import collaborate.api.datasource.businessdata.access.GrantAccessTransactionHandler;
import collaborate.api.datasource.businessdata.access.RequestAccessTransactionHandler;
import collaborate.api.datasource.businessdata.kpi.CreatedDatasourceTransactionHandler;
import collaborate.api.datasource.businessdata.kpi.CreatedScopeTransactionHandler;
import collaborate.api.transaction.TezosApiGatewayTransactionClient;
import collaborate.api.transaction.TransactionEventManager;
import collaborate.api.transaction.TransactionPersistenceHandler;
import collaborate.api.transaction.TransactionStateService;
import collaborate.api.transaction.TransactionWatcher;
import collaborate.api.transaction.TransactionWatchersProperties;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
@ConditionalOnExpression("!'${smartContractAddress.businessData}'.isEmpty()")
public class BusinessDataTransactionWatcher {

  private final String businessDataContractAddress;
  private final CreatedDatasourceTransactionHandler createdDatasourceTransactionHandler;
  private final CreatedScopeTransactionHandler createdScopeTransactionHandler;
  private final GrantAccessTransactionHandler grantAccessTransactionHandler;
  private final RequestAccessTransactionHandler requestAccessTransactionHandler;
  private final TezosApiGatewayTransactionClient tezosApiGatewayTransactionClient;
  private final ThreadPoolTaskScheduler transactionWatcherPoolTaskScheduler;
  private final TransactionPersistenceHandler transactionPersistenceHandler;
  private final TransactionWatchersProperties watchersProperties;
  private final TransactionStateService transactionStateService;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (StringUtils.isNotBlank(businessDataContractAddress)) {
      transactionWatcherPoolTaskScheduler.schedule(
          buildWatcher(),
          buildPeriodicTrigger()
      );
    }
  }

  private TransactionWatcher buildWatcher() {
    return new TransactionWatcher(
        businessDataContractAddress,
        initBusinessDataEventManager(),
        tezosApiGatewayTransactionClient,
        transactionStateService
    );
  }

  private PeriodicTrigger buildPeriodicTrigger() {
    return new PeriodicTrigger(
        watchersProperties.getFixedDelayInMs(),
        TimeUnit.MILLISECONDS
    );
  }

  private TransactionEventManager initBusinessDataEventManager() {
    log.info("Initializing block chain transaction event manager");
    var transactionEventManager = new TransactionEventManager();
    transactionEventManager.subscribe(createdDatasourceTransactionHandler);
    transactionEventManager.subscribe(createdScopeTransactionHandler);
    transactionEventManager.subscribe(transactionPersistenceHandler);
    transactionEventManager.subscribe(grantAccessTransactionHandler);
    transactionEventManager.subscribe(requestAccessTransactionHandler);
    return transactionEventManager;
  }

}
