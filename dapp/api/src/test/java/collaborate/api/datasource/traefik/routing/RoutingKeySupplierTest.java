package collaborate.api.datasource.traefik.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import org.junit.jupiter.api.Test;

class RoutingKeySupplierTest {

  @Test
  void get_shouldReturnKeywordWithoutRoutingKeyPrefix_withScopePrefix() {
    // GIVEN
    List<String> keywords = List.of("scope:metric:odometer");
    // WHEN
    var route = new RoutingKeyFromKeywordSupplier(keywords).get();
    // THEN
    assertThat(route).isEqualTo("scope:metric:odometer");
  }

  @Test
  void get_shouldReturnKeywordWithoutRoutingKeyPrefix_withPurposePrefix() {
    // GIVEN
    List<String> keywords = List.of("purpose:test");
    // WHEN
    var route = new RoutingKeyFromKeywordSupplier(keywords).get();
    // THEN
    assertThat(route).isEqualTo("purpose:test");
  }

  @Test
  void get_shouldReturnFirstKeywordWithoutRoutingKeyPrefix_withScopePrefix() {
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
