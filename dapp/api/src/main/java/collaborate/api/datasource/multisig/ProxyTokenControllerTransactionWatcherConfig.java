package collaborate.api.datasource.multisig;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.businessdata.access.GrantAccessTransactionHandler;
import collaborate.api.datasource.businessdata.kpi.CreatedDatasourceTransactionHandler;
import collaborate.api.datasource.businessdata.kpi.CreatedScopeTransactionHandler;
import collaborate.api.transaction.TezosApiGatewayTransactionClient;
import collaborate.api.transaction.TransactionEventManager;
import collaborate.api.transaction.TransactionProperties;
import collaborate.api.transaction.TransactionStateService;
import collaborate.api.transaction.TransactionWatcher;
import collaborate.api.transaction.TransactionWatcherProperty;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
@ConditionalOnProperty(prefix = "transaction", name = "enabled", havingValue = "true")
public class ProxyTokenControllerTransactionWatcherConfig {

  private final ApiProperties apiProperties;
  private final BuildMultiSigHandler buildMultiSigHandler;
  private final TezosApiGatewayTransactionClient tezosApiGatewayTransactionClient;
  private final ThreadPoolTaskScheduler transactionWatcherPoolTaskScheduler;
  private final TransactionProperties transactionProperties;
  private final TransactionStateService transactionStateService;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    for (var watcherProperty : transactionProperties.getWatchers()) {
      if (watcherProperty.isSmartContract(apiProperties.getDigitalPassportProxyTokenControllerContractAddress())) {
        transactionWatcherPoolTaskScheduler.schedule(
            buildWatcher(watcherProperty),
            buildPeriodicTrigger(watcherProperty)
        );
      }
    }
  }

  private TransactionWatcher buildWatcher(TransactionWatcherProperty watcherProperty) {
    return new TransactionWatcher(
        watcherProperty.getSmartContractAddress(),
        initBusinessDataEventManager(),
        tezosApiGatewayTransactionClient,
        transactionStateService
    );
  }

  private PeriodicTrigger buildPeriodicTrigger(
      TransactionWatcherProperty transactionWatcherProperty) {
    return new PeriodicTrigger(
        transactionWatcherProperty.getFixedDelayInMs(),
        TimeUnit.MILLISECONDS
    );
  }

  private TransactionEventManager initBusinessDataEventManager() {
    var transactionEventManager = new TransactionEventManager();
    transactionEventManager.subscribe(buildMultiSigHandler);
    return transactionEventManager;
  }

}
