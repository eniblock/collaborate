package collaborate.api.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheService {

  private final CacheManager cacheManager;

  public void clearOrThrow(String cacheName) {
    var cache = cacheManager.getCache(cacheName);
    if (cache == null){
      throw new IllegalStateException(String.format("Cache name=%s does not exist", cacheName));
    }
    cache.clear();
  }

}
