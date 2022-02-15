package collaborate.api.datasource.gateway.datasource.traefik.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoutingKeySupplierTest {

  @Test
  void get_shouldReturnKeyword_withScopePrefix() {
    // GIVEN
    List<String> keywords = List.of("scope:metric:odometer");
    // WHEN
    var route = new RoutingKeyFromKeywordSupplier(keywords).get();
    // THEN
    assertThat(route).isEqualTo("scope:metric:odometer");
  }

  @Test
  void get_shouldReturnKeyword_withAssetList() {
    // GIVEN
    List<String> keywords = List.of("list-asset");
    // WHEN
    var route = new RoutingKeyFromKeywordSupplier(keywords).get();
    // THEN
    assertThat(route).isEqualTo("list-asset");
  }

  @Test
  void get_shouldReturnFirstKeyword_withScopePrefix() {
    // GIVEN
    List<String> keywords = List.of("scope:routeA", "scope:routeB");
    // WHEN
    var route = new RoutingKeyFromKeywordSupplier(keywords).get();
    // THEN
    assertThat(route).isEqualTo("scope:routeA");
  }

  @Test
  void get_shouldThrowException_withInvalidKeywords() {
    // GIVEN
    List<String> keywords = List.of("routeA", "routeB");
    // THEN
    assertThatExceptionOfType(IllegalStateException.class)
        // WHEN
        .isThrownBy(() -> new RoutingKeyFromKeywordSupplier(keywords));
  }
}
