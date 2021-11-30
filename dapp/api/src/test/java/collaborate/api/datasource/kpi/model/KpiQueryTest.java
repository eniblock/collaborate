package collaborate.api.datasource.kpi.model;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.kpi.find.KpiQuery;
import collaborate.api.datasource.kpi.find.SearchCriteria;
import collaborate.api.test.TestResources;
import java.util.Set;
import org.junit.jupiter.api.Test;

class KpiQueryTest {

  @Test
  void serialization() {
    // GIVEN
    // WHEN
    var kpiQuery = TestResources.readContent("/datasource/kpi/find/kpi-query-sample.json",
        KpiQuery.class);
    // THEN
    assertThat(kpiQuery).isEqualTo(KpiQuery.builder()
        .labelFormat("YYYY-MM")
        .labelGroup("createdAt")
        .dataSetsGroup("organizationWallet")
        .search(Set.of(new SearchCriteria("kpiKey", "=", "digital-passport")))
        .build());
  }


}
