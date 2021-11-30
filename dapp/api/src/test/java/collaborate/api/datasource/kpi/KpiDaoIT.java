package collaborate.api.datasource.kpi;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.ApiApplication;
import collaborate.api.test.SharedDatabaseTest;
import collaborate.api.test.database.PostgresqlSharedTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SharedDatabaseTest
@ContextConfiguration(
    classes = {
        PostgresqlSharedTestContainer.Config.class,
        ApiApplication.class
    }
)
class KpiDaoIT {

  @Container
  public static PostgreSQLContainer<?> postgreSQLContainer = PostgresqlSharedTestContainer.getInstance();

  @Autowired
  KpiDAO kpiDAO;

  @Test
  void kpiDAO_isInitialized() {
    assertThat(kpiDAO).isNotNull();
  }

}
