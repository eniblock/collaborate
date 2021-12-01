package collaborate.api.datasource.kpi;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.ApiApplication;
import collaborate.api.test.SharedDatabaseTest;
import collaborate.api.test.TestResources;
import collaborate.api.test.database.PostgresqlSharedTestContainer;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;
import javax.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
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

  @ParameterizedTest
  @MethodSource("kpiSpecification_findAllParameters")
  @Transactional
  void kpiSpecification_findAll(String testMessage,
      List<JsonSpecification> specifications, List<ZonedDateTime> expectedId) {
    // GIVEN
    var kpis = TestResources.readContent(
        "/datasource/kpi/kpis.json",
        new TypeReference<List<Kpi>>() {
        });
    kpiDAO.saveAll(kpis);

    var kpiSpecs = new KpiSpecification(specifications);
    // WHEN
    var kpiResults = kpiDAO.findAll(kpiSpecs);
    // THEN
    assertThat(extractCreatedAt(kpiResults)).as(testMessage)
        .hasSameElementsAs(expectedId);
  }

  private static Stream<Arguments> kpiSpecification_findAllParameters() {
    return Stream.of(
        Arguments.of(
            "with single JsonSpecification matching multiple KPIs",
            List.of(
                new JsonSpecification("requester", "orgA")
            ),
            List.of(
                buildZonedDatetime("2021-01-01T00:00:02.000Z"),
                buildZonedDatetime("2021-02-01T00:00:02.000Z")
            ),
            Arguments.of(
                "withMultipleJsonSpecification",
                List.of(
                    new JsonSpecification("requester", "orgA"),
                    new JsonSpecification("scope", "scopeA")
                ),
                List.of(
                    buildZonedDatetime("2021-01-01T00:00:02.000Z"))
            )
        )
    );
  }

  @NotNull
  private static ZonedDateTime buildZonedDatetime(String s) {
    return ZonedDateTime.parse(s).withZoneSameLocal(ZoneId.of("UTC"));
  }

  public List<ZonedDateTime> extractCreatedAt(List<Kpi> kpis) {
    return kpis.stream().map(Kpi::getCreatedAt).collect(toList());
  }
}
