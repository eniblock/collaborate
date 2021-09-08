package collaborate.api.cache;

import static collaborate.api.cache.CacheConfig.CacheNames.DATASOURCE;
import static collaborate.api.cache.CacheConfig.CacheNames.ORGANIZATION;
import static collaborate.api.cache.CacheConfig.CacheNames.USER;
import static collaborate.api.cache.CacheConfig.CacheNames.VEHICLE_ID;
import static collaborate.api.cache.CacheConfig.CacheNames.WEBSERVER_DATASOURCE;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final class CacheNames {
        public static final String DATASOURCE = "datasource";
        public static final String ORGANIZATION = "organization";
        public static final String USER = "user";
        public static final String VEHICLE_ID = "vehicleId";
        public static final String WEBSERVER_DATASOURCE = "werServerDatasource";

        private CacheNames() {
        }
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(DATASOURCE, ORGANIZATION, USER, VEHICLE_ID, WEBSERVER_DATASOURCE);
    }
}