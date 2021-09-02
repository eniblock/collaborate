package collaborate.api.datasource.traefik.routing;

import static collaborate.api.datasource.domain.web.CertificateBasedBasicAuthDatasourceFeatures.getResourceByKeyword;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.domain.web.WebServerResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class FromRouteRegexSupplierTest {

  final String datasourceName = "ds1";
  final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void get_shouldReturnExpectedPath_withoutQueryParams() throws JsonProcessingException {
    // GIVEN
    String resourceKeyword = "vehicles";
    var resource = objectMapper
        .readValue(objectMapper.writeValueAsString(
            getResourceByKeyword(resourceKeyword)),
            WebServerResource.class);
    // WHEN
    var supplier = new FromRouteRegexSupplier(datasourceName, resource);
    // THEN
    assertThat(supplier.get()).isEqualTo("/datasource/ds1/" + resourceKeyword);
  }

  @Test
  void get_shouldReturnExpectedPath_with1QueryParams() {
    // GIVEN
    String resourceKeyword = "kilometer";
    var resource = getResourceByKeyword(resourceKeyword);
    // WHEN
    var supplier = new FromRouteRegexSupplier(datasourceName, resource);
    // THEN
    assertThat(supplier.get()).isEqualTo("/datasource/ds1/" + resourceKeyword + "/(.*)");
  }

  @Test
  void get_shouldReturnExpectedPath_with2QueryParams() {
    // GIVEN
    String resourceKeyword = "maintenance";
    var resource = getResourceByKeyword(resourceKeyword);
    // WHEN
    var supplier = new FromRouteRegexSupplier(datasourceName, resource);
    // THEN
    assertThat(supplier.get()).isEqualTo("/datasource/ds1/" + resourceKeyword + "/(.*)/(.*)");
  }
}
