package collaborate.api.datasource.kpi;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.ApiApplication;
import collaborate.api.datasource.kpi.model.Kpi;
import collaborate.api.datasource.kpi.model.KpiDataSet;
import collaborate.api.datasource.kpi.model.KpiQuery;
import collaborate.api.organization.tag.Organization;
import collaborate.api.test.SharedDatabaseTest;
import collaborate.api.test.TestResources;
import collaborate.api.test.database.PostgresqlSharedTestContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SharedDatabaseTest
@Slf4j
@ContextConfiguration(
    classes = {
        PostgresqlSharedTestContainer.Config.class,
        ApiApplication.class,
        KpiService.class
    }
)
class KpiServiceIT {

  @Container
  public static PostgreSQLContainer<?> postgreSQLContainer = PostgresqlSharedTestContainer.getInstance();

  @Autowired
  KpiDAO kpiDAO;
  @Autowired
  KpiService kpiService;

  @Test
  void find_shouldResultWithDataSetsHavingAsManyValuesAsLabels() {
    // GIVEN
    insertKpis("/datasource/kpi/kpis.json");

    var kpiQuery = TestResources.readContent(
        "/datasource/kpi/kpi-query-by-month.json",
        KpiQuery.class
    );

    // WHEN
    var kpiResult = kpiService.find(kpiQuery);

    // THEN
    assertThat(kpiResult.getKpiKey()).isEqualTo("digital-passport");
    assertThat(kpiResult.getLabels()).containsExactly("2021-01", "2021-02", "2021-10", "2021-12");
    assertThat(kpiResult.getDataSets()).containsExactly(
        new KpiDataSet(
            Organization.builder().address("orgA").build(),
            List.of(0L, 0L, 2L, 0L)
        ),
        new KpiDataSet(
            Organization.builder().address("orgB").build(),
            List.of(1L, 1L, 0L, 1L)
        )
    );
  }

  private void insertKpis(String resourcePath) {
    var kpis = TestResources.readContent(
        resourcePath,
        new TypeReference<List<Kpi>>() {
        });
    kpiDAO.saveAll(kpis);
  }


}
