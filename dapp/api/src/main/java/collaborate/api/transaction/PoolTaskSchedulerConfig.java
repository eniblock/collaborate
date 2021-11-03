package collaborate.api.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@RequiredArgsConstructor
public class PoolTaskSchedulerConfig {

  private final TransactionProperties transactionProperties;

  @Bean
  @ConditionalOnProperty(name = "transaction.watchers[0].fixedDelayInMs")
  public ThreadPoolTaskScheduler transactionWatcherPoolTaskScheduler() {
    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    threadPoolTaskScheduler.setPoolSize(transactionProperties.getWatchers().size());
    threadPoolTaskScheduler.setThreadNamePrefix("TransactionWatcherPoolTaskScheduler");
    return threadPoolTaskScheduler;
  }
}