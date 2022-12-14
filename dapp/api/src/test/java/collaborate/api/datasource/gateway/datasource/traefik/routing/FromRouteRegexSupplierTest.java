package collaborate.api.datasource.gateway.datasource.traefik.routing;

import static collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures.getResourceByAlias;
import static collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures.getResourceHavingKeywordName;
import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.gateway.traefik.routing.FromRouteRegexSupplier;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

class FromRouteRegexSupplierTest {

  final String datasourceName = "ds1";

  @Test
  void get_shouldReturnExpectedPath_withoutQueryParams() throws JsonProcessingException {
    // GIVEN
    String resourceKeyword = "list-asset";
    var resource = objectMapper
        .readValue(objectMapper.writeValueAsString(
                getResourceHavingKeywordName(resourceKeyword)),
            WebServerResource.class);
    // WHEN
    var supplier = new FromRouteRegexSupplier(datasourceName, resource);
    // THEN
    assertThat(supplier.get()).isEqualTo("/datasource/ds1/list-asset");
  }

  @Test
  void get_shouldReturnExpectedPath_with1QueryParams() {
    // GIVEN
    String resourceKeyword = "metric:odometer";
    var resource = getResourceByAlias(resourceKeyword);
    // WHEN
    var supplier = new FromRouteRegexSupplier(datasourceName, resource);
    // THEN
    assertThat(supplier.get()).isEqualTo("/datasource/ds1/metric:odometer/(.*)");
  }

}
