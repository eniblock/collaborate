package collaborate.api.test.database;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
public class PostgresqlSharedTestContainer extends
    PostgreSQLContainer<PostgresqlSharedTestContainer> {

  private static final String IMAGE_VERSION = "postgres";
  private static PostgresqlSharedTestContainer container;

  private PostgresqlSharedTestContainer() {
    super(IMAGE_VERSION);
  }

  public static PostgresqlSharedTestContainer getInstance() {
    if (container == null) {
      container = new PostgresqlSharedTestContainer();
    }
    return container;
  }

  @Override
  public void start() {
    super.start();
    System.setProperty("DB_URL", container.getJdbcUrl());
    System.setProperty("DB_USERNAME", container.getUsername());
    System.setProperty("DB_PASSWORD", container.getPassword());
    log.info("Test Database url={}, user={}, password={}",
        container.getJdbcUrl(),
        container.getUsername(),
        container.getPassword()
    );
  }

  @Override
  public void stop() {
    //do nothing, JVM handles shut down
  }

  @Configuration
  public static class Config {

    @Bean
    public DataSource dataSource() {
      final DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setUrl(container.getJdbcUrl());
      dataSource.setUsername(container.getUsername());
      dataSource.setPassword(container.getPassword());
      return dataSource;

    }
  }
}
