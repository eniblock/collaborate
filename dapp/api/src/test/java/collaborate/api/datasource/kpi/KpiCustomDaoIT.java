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
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

  @ParameterizedTest
  @MethodSource("getSearchParameters")
  void search(String testMessage,
      List<SearchCriteria> search, List<KpiAggregation> expectedResult) {
    // GIVEN
    var kpis = TestResources.readContent(
        "/datasource/kpi/kpis.json",
        new TypeReference<List<Kpi>>() {
        });
    kpiDAO.saveAll(kpis);

    var kpiQuery = buildQuery();
    kpiQuery.getSearch().addAll(search);
    // WHEN
    var aggregationResult = kpiCustomDAO.search(kpiQuery);

    // THEN
    assertThat(aggregationResult).as(testMessage)
        .containsExactlyElementsOf(expectedResult);
  }

  private static Stream<Arguments> getSearchParameters() {
    return Stream.of(
        Arguments.of(
            "search_withFilterOnKpiKey",
            List.of(new SearchCriteria("kpiKey", "=", "digital-passport")),
            List.of(
                new KpiAggregation("orgA", "2021-10", 2L),
                new KpiAggregation("orgB", "2021-01", 1L),
                new KpiAggregation("orgB", "2021-02", 1L),
                new KpiAggregation("orgB", "2021-12", 1L)
            )
        ),
        Arguments.of(
            "search_withFilterOnOrganizationWallet",
            List.of(new SearchCriteria("values.first.secondInt", "=", "2")),
            List.of(new KpiAggregation("orgA", "2021-10", 1L))
        ),
        Arguments.of(
            "search_withEqualFilterOnValuesJsonFirstLevelField",
            List.of(new SearchCriteria("values.first.secondInt", "=", "2")),
            List.of(new KpiAggregation("orgA", "2021-10", 1L))
        ),
        Arguments.of(
            "search_withEqualFilterOnValuesJsonSecondLevelField",
            List.of(new SearchCriteria("values.first.secondInt", "=", "2")),
            List.of(new KpiAggregation("orgA", "2021-10", 1L))
        ), Arguments.of(
            "search_withContainFilterOnValuesJsonSecondLevelField",
            List.of(new SearchCriteria("values.first.secondString", ":", "2nd level")),
            List.of(new KpiAggregation("orgB", "2021-12", 1L))
        ),
        Arguments.of(
            "withMultipleFilterOnValuesJsonSecondLevelField",
            List.of(
                new SearchCriteria("values.requester", "=", "orgA"),
                new SearchCriteria("values.scope", "=", "scopeA")
            ),
            List.of(
                new KpiAggregation("orgB", "2021-01", 1L)
            )
        ),
        Arguments.of(
            "withGreaterThanFilterOnCeatedAt",
            List.of(
                new SearchCriteria("createdAt", ">", "2021-11-01T00:00:00.000Z")
            ),
            List.of(
                new KpiAggregation("orgB", "2021-12", 1L)
            )
        )
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


}
