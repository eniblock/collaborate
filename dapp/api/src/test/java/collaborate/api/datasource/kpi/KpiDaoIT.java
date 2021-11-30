package collaborate.api.datasource.kpi;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.ApiApplication;
import collaborate.api.datasource.kpi.model.Kpi;
import collaborate.api.test.SharedDatabaseTest;
import collaborate.api.test.TestResources;
import collaborate.api.test.database.PostgresqlSharedTestContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;
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
  void save() {
    assertThat(kpiDAO.count()).isZero();

    var kpi = Kpi.builder()
        .kpiKey("passport")
        .build();

    kpiDAO.save(kpi);
    assertThat(kpiDAO.count()).isPositive();
  }

  @Test
  void saveWithJsonNode() {
    assertThat(kpiDAO.count()).isZero();

    var parameters = TestResources.objectMapper.convertValue(Map.of("p", "v"), JsonNode.class);
    var kpi = Kpi.builder()
        .kpiKey("passport")
        .parameters(parameters)
        .build();

    var k = kpiDAO.save(kpi);
    assertThat(kpiDAO.count()).isPositive();
  }

  @Test
  void save_nullParamaters() {
    // GIVEN
    var kpi = Kpi.builder()
        .kpiKey("passport")
        .build();

    var kpiResult = kpiDAO.save(kpi);
    assertThat(kpiDAO.count()).isPositive();
    assertThat(kpiResult.getParameters()).isNull();
  }

  @Test
  void countByOrganizationAndYear() {
    // GIVEN
    var kpis = TestResources.readContent(
        "/datasource/kpi/kpis.json",
        new TypeReference<List<Kpi>>() {
        });
    kpiDAO.saveAll(kpis);
    assertThat(kpiDAO.count()).isEqualTo(7);

    // WHEN
    var countResult = kpiDAO.countByKeyAndDatetime("digital-passport", "YYYY-MM");
    assertThat(countResult).hasSize(8);
    assertThat(countResult.stream().filter(a -> a.getDataSetsGroup().equals("orgA")))
        .hasSize(4);
    assertThat(countResult.stream().filter(a -> a.getDataSetsGroup().equals("orgB")))
        .hasSize(4);
  }

}
