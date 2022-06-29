package collaborate.api.datasource.kpi;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.ApiApplication;
import collaborate.api.datasource.kpi.find.FindKpiCustomDAO;
import collaborate.api.datasource.kpi.find.KpiAggregation;
import collaborate.api.datasource.kpi.find.KpiQuery;
import collaborate.api.datasource.kpi.find.SearchCriteria;
import collaborate.api.test.SharedDatabaseTest;
import collaborate.api.test.database.PostgresqlSharedTestContainer;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
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
        FindKpiCustomDAO.class
    }
)
class KpiCustomDaoIT {

  @Container
  public static PostgreSQLContainer<?> postgreSQLContainer = PostgresqlSharedTestContainer.getInstance();

  @Autowired
  KpiDAO kpiDAO;
  @Autowired
  FindKpiCustomDAO findKpiCustomDAO;

  @ParameterizedTest
  @MethodSource("getSearchParameters")
  void findByKpiQuery(String testMessage,
      List<SearchCriteria> search, List<KpiAggregation> expectedResult) {
    // GIVEN
    kpiDAO.saveAll(KpiFeatures.kpis);

    var kpiQuery = buildQuery();
    kpiQuery.getSearch().addAll(search);
    // WHEN
    var aggregationResult = findKpiCustomDAO.find(kpiQuery);

    // THEN
    assertThat(aggregationResult).as(testMessage)
        .containsExactlyElementsOf(expectedResult);
  }

  private static Stream<Arguments> getSearchParameters() {
    return Stream.of(
        Arguments.of(
            "search_withContainFilterOnKpiKey",
            List.of(new SearchCriteria("organizationWallet", ":", "A")),
            List.of(
                new KpiAggregation("orgA", "2021-10", 2L)
            )
        ),
        Arguments.of(
            "search_withEqualFilterOnKpiKey",
            List.of(new SearchCriteria("kpiKey", "=", "digital-passport")),
            List.of(
                new KpiAggregation("orgA", "2021-10", 2L),
                new KpiAggregation("orgB", "2021-01", 1L),
                new KpiAggregation("orgB", "2021-02", 1L),
                new KpiAggregation("orgB", "2021-12", 1L)
            )
        ),
        Arguments.of(
            "search_withEqualFilterOnOrganizationWallet",
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
        ),
        Arguments.of(
            "search_withEqualFilterOnCreatedAt",
            List.of(new SearchCriteria("createdAt", "=", "2021-12-31T00:00:02.000Z")),
            List.of(new KpiAggregation("orgB", "2021-12", 1L))
        ),
        Arguments.of(
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
        .labelFormat("YYYY-MM")
        .labelGroup("createdAt")
        .dataSetsGroup("organizationWallet")
        .search(new HashSet<>())
        .build();
  }

  @Test
  void findByCriteria() {
    // GIVEN
    kpiDAO.deleteAll();
    kpiDAO.saveAll(KpiFeatures.kpis);

    // WHEN
    var searchCriteria = List.of(
        new SearchCriteria("values.requester", "=", "orgA")
    );
    var aggregationResult = findKpiCustomDAO.find(searchCriteria);
    // THEN
    var expectedKpis = kpiDAO.findAll().stream().filter(
            kpi ->
                Optional.ofNullable(kpi.getValues())
                    .map(values -> values.get("requester"))
                    .map(requester -> requester.asText().equals("orgA"))
                    .orElse(false))
        .collect(Collectors.toList());

    assertThat(aggregationResult)
        .containsExactlyElementsOf(expectedKpis);
  }
}
