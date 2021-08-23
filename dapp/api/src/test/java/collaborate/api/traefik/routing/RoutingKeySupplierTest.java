package collaborate.api.traefik.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import org.junit.jupiter.api.Test;

class RoutingKeySupplierTest {

  @Test
  void get_shouldReturnKeywordWithoutRoutingKeyPrefix_withOnValidKeyword() {
    // GIVEN
    List<String> keywords = List.of("routing-key:route");
    // WHEN
    var route = new RoutingKeyKeywordSupplier(keywords).get();
    // THEN
    assertThat(route).isEqualTo("route");
  }

  @Test
  void get_shouldReturnFirstKeywordWithoutRoutingKeyPrefix_withOnValidKeywords() {
    // GIVEN
    List<String> keywords = List.of("routing-key:routeA", "routing-key:routeB");
    // WHEN
    var route = new RoutingKeyKeywordSupplier(keywords).get();
    // THEN
    assertThat(route).isEqualTo("routeA");
  }

  @Test
  void get_shouldThrowException_withInvalidKeywords() {
    // GIVEN
    List<String> keywords = List.of("routeA", "routeB");
    // THEN
    assertThatExceptionOfType(IllegalStateException.class)
        // WHEN
        .isThrownBy(() -> new RoutingKeyKeywordSupplier(keywords));
  }
}
