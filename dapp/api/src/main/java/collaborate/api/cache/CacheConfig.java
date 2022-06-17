package collaborate.api.cache;

import static collaborate.api.cache.CacheConfig.CacheNames.DATASOURCE;
import static collaborate.api.cache.CacheConfig.CacheNames.IPNS_KEY_BY_PATH;
import static collaborate.api.cache.CacheConfig.CacheNames.ORGANIZATION;
import static collaborate.api.cache.CacheConfig.CacheNames.USER;
import static collaborate.api.cache.CacheConfig.CacheNames.VEHICLE_ID;
import static collaborate.api.cache.CacheConfig.CacheNames.WEBSERVER_DATASOURCE;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

  public static final class CacheNames {

    public static final String DATASOURCE = "datasource";
    public static final String IPNS_KEY_BY_PATH = "ipns-key-by-path";
    public static final String ORGANIZATION = "organization";
    public static final String USER = "user";
    public static final String VEHICLE_ID = "vehicleId";
    public static final String WEBSERVER_DATASOURCE = "werServerDatasource";

    private CacheNames() {
    }
  }

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager(
        DATASOURCE,
        IPNS_KEY_BY_PATH,
        ORGANIZATION,
        USER,
        VEHICLE_ID,
        WEBSERVER_DATASOURCE);
  }

}
