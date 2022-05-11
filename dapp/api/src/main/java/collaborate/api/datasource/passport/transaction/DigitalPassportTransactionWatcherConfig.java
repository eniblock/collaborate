package collaborate.api.datasource.passport.transaction;

import collaborate.api.transaction.TezosApiGatewayTransactionClient;
import collaborate.api.transaction.TransactionEventManager;
import collaborate.api.transaction.TransactionProperties;
import collaborate.api.transaction.TransactionStateService;
import collaborate.api.transaction.TransactionWatcher;
import collaborate.api.transaction.TransactionWatcherProperty;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
@ConditionalOnExpression("!'${smartContractAddress.digitalPassport}'.isEmpty()")
public class DigitalPassportTransactionWatcherConfig {

  private final String businessDataContractAddress;
  private final MintTokenHandler mintTokenHandler;
  private final TezosApiGatewayTransactionClient tezosApiGatewayTransactionClient;
  private final ThreadPoolTaskScheduler transactionWatcherPoolTaskScheduler;
  private final TransactionProperties transactionProperties;
  private final TransactionStateService transactionStateService;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    for (var watcherProperty : transactionProperties.getWatchers()) {
      if (watcherProperty.isSmartContract(businessDataContractAddress)) {
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
        initDigitalPassportEventManager(),
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

  private TransactionEventManager initDigitalPassportEventManager() {
    log.info("Initializing block chain transaction event manager");
    var transactionEventManager = new TransactionEventManager();
    transactionEventManager.subscribe(mintTokenHandler);
    return transactionEventManager;
  }

}