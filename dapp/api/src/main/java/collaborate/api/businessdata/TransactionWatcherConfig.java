package collaborate.api.businessdata;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.transaction.TransactionEventManager;
import collaborate.api.transaction.TransactionProperties;
import collaborate.api.transaction.TransactionWatcher;
import collaborate.api.transaction.TransactionWatcherProperty;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class TransactionWatcherConfig {

  private final ThreadPoolTaskScheduler transactionWatcherPoolTaskScheduler;
  private final TransactionProperties transactionProperties;
  private final ApiProperties apiProperties;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    for (var watcherProperty : transactionProperties.getWatchers()) {
      if (watcherProperty.isSmartContract(apiProperties.getBusinessDataContractAddress())) {
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
        initBusinessDataEventManager()
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
    // TODO The differents handlers muust be defined here
    return new TransactionEventManager(List.of(
        transaction -> log.info("{}", transaction)
    ));
  }

}
