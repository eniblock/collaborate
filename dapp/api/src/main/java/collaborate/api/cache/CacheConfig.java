package collaborate.api.cache;

import static collaborate.api.cache.CacheConfig.CacheNames.ORGANIZATIONS;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final class CacheNames {
        public static final String ORGANIZATIONS = "organizations";

        private CacheNames() {
        }
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(ORGANIZATIONS);
    }
}