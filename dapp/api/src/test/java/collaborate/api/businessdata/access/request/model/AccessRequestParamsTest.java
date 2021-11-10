package collaborate.api.businessdata.access.request.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class AccessRequestParamsTest {

  final String datasourceId = "datasourceId";
  final String datasourceScope = "datasourceScope";
  final AccessRequestParams accessRequestParams = AccessRequestParams.builder()
      .scopes(List.of(datasourceId + ":" + datasourceScope))
      .build();

  @Test
  void getDatasourceId_shouldReturnDatasourceId() {
    // GIVEN
    // WHEN
    var datasourceIdResult = accessRequestParams.getDatasourceId();
    // THEN
    assertThat(datasourceIdResult).isEqualTo(datasourceId);
  }

  @Test
  void getDatasourceScope_shouldReturnDatasourceScope() {
    // GIVEN
    // WHEN
    var datasourceScopeResult = accessRequestParams.getDatasourceScope();
    // THEN
    assertThat(datasourceScopeResult).isEqualTo(datasourceScope);
  }
}
