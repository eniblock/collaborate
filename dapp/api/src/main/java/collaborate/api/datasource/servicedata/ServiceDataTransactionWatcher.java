package collaborate.api.datasource.servicedata;

//import collaborate.api.datasource.servicedata.kpi.CreatedServiceScopeTransactionHandler;
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
@ConditionalOnExpression("!'${smartContractAddress.serviceData}'.isEmpty()")
public class ServiceDataTransactionWatcher {

  private final String serviceDataContractAddress;
  private final CreatedServiceDataTransactionHandler createdServiceDataTransactionHandler;
  //private final CreatedServiceScopeTransactionHandler createdServiceScopeTransactionHandler;
  private final TezosApiGatewayTransactionClient tezosApiGatewayTransactionClient;
  private final ThreadPoolTaskScheduler transactionWatcherPoolTaskScheduler;
  private final TransactionPersistenceHandler transactionPersistenceHandler;
  private final TransactionWatchersProperties watchersProperties;
  private final TransactionStateService transactionStateService;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (StringUtils.isNotBlank(serviceDataContractAddress)) {
      transactionWatcherPoolTaskScheduler.schedule(
          buildWatcher(),
          buildPeriodicTrigger()
      );
    }
  }

  private TransactionWatcher buildWatcher() {
    return new TransactionWatcher(
        serviceDataContractAddress,
        initServiceDataEventManager(),
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

  private TransactionEventManager initServiceDataEventManager() {
    log.info("Initializing block chain transaction event manager");
    var transactionEventManager = new TransactionEventManager();
    transactionEventManager.subscribe(createdServiceDataTransactionHandler);
    //transactionEventManager.subscribe(createdServiceScopeTransactionHandler);
    transactionEventManager.subscribe(transactionPersistenceHandler);
    return transactionEventManager;
  }

}
