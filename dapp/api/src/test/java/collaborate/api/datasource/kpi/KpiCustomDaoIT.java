package collaborate.api.datasource.kpi;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.ApiApplication;
import collaborate.api.datasource.kpi.model.Kpi;
import collaborate.api.datasource.kpi.model.KpiAggregation;
import collaborate.api.datasource.kpi.model.KpiQuery;
import collaborate.api.datasource.kpi.model.SearchCriteria;
import collaborate.api.test.SharedDatabaseTest;
import collaborate.api.test.TestResources;
import collaborate.api.test.database.PostgresqlSharedTestContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SharedDatabaseTest
@ContextConfiguration(
    classes = {
        PostgresqlSharedTestContainer.Config.class,
        ApiApplication.class,
        KpiCustomDAO.class
    }
)
class KpiCustomDaoIT {

  @Container
  public static PostgreSQLContainer<?> postgreSQLContainer = PostgresqlSharedTestContainer.getInstance();

  @Autowired
  KpiDAO kpiDAO;
  @Autowired
  KpiCustomDAO kpiCustomDAO;

  @Test
  void search_withFilterOnKpiKey() {
    // GIVEN
    var kpis = TestResources.readContent(
        "/datasource/kpi/kpis.json",
        new TypeReference<List<Kpi>>() {
        });
    kpiDAO.saveAll(kpis);

    // WHEN
    var kpiQuery = TestResources.readContent(
        "/datasource/kpi/kpi-key-groupBy-wallet-and-createdAt.json",
        KpiQuery.class
    );

    var kpiAggregationsResult = kpiCustomDAO.search(kpiQuery);

    // THEN
    assertThat(kpiAggregationsResult).containsExactlyInAnyOrder(
        new KpiAggregation("orgA", "2021-10", 2L),
        new KpiAggregation("orgB", "2021-01", 1L),
        new KpiAggregation("orgB", "2021-02", 1L),
        new KpiAggregation("orgB", "2021-12", 1L)
    );
  }

  public KpiQuery buildQuery() {
    return KpiQuery.builder()
        .datetimeFormat("YYYY-MM")
        .labelGroup("createdAt")
        .dataSetsGroup("organizationWallet")
        .search(new HashSet<>())
        .build();
  }

  @Test
  void search_withFilterOnOrganizationWallet() {
    // GIVEN
    var kpis = TestResources.readContent(
        "/datasource/kpi/kpis.json",
        new TypeReference<List<Kpi>>() {
        });
    kpiDAO.saveAll(kpis);

    var kpiQuery = buildQuery();
    kpiQuery.getSearch().add(new SearchCriteria("organizationWallet", "=", "orgA"));
    // WHEN
    var r = kpiCustomDAO.search(kpiQuery);

    // THEN
    assertThat(r).containsExactlyInAnyOrder(
        new KpiAggregation("orgA", "2021-10", 2L)
    );
  }
}
