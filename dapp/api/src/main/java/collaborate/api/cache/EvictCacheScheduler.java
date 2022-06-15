package collaborate.api.cache;

import collaborate.api.cache.CacheConfig.CacheNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EvictCacheScheduler {

  private final CacheService cacheService;

  @Scheduled(fixedRate = 2500)
  public void clearOrganizations() {
   cacheService.clearOrThrow(CacheNames.ORGANIZATION);
  }

}
