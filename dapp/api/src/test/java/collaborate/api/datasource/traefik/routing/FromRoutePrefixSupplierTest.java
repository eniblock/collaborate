package collaborate.api.datasource.traefik.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class FromRoutePrefixSupplierTest {

  @Test
  void get_shouldReturnExpected_withSimpleScopePrefix() {
    // GIVEN
    var supplier = new FromRoutePrefixSupplier("datasource-name", Set.of("scope:odometer"));
    // WHEN
    var routePrefixResult = supplier.get();
    // THEN
    assertThat(routePrefixResult).isEqualTo("/datasource/datasource-name/scope:odometer");
  }

  @Test
  void get_shouldReturnExpected_withComposedScopePrefix() {
    // GIVEN
    var supplier = new FromRoutePrefixSupplier("datasource-name", Set.of("scope:metric:odometer"));
    // WHEN
    var routePrefixResult = supplier.get();
    // THEN
    assertThat(routePrefixResult).isEqualTo("/datasource/datasource-name/scope:metric:odometer");
  }

  @Test
  void get_shouldReturnExpected_withPurposePrefix() {
    // GIVEN
    var supplier = new FromRoutePrefixSupplier("datasource-name", Set.of("purpose:test"));
    // WHEN
    var routePrefixResult = supplier.get();
    // THEN
    assertThat(routePrefixResult).isEqualTo("/datasource/datasource-name/purpose:test");
  }

  @Test
  void get_shouldThrowException_withInvalidKeywords() {
    // GIVEN
    List<String> keywords = List.of("routeA", "routeB");
    // THEN
    assertThatExceptionOfType(IllegalStateException.class)
        // WHEN
        .isThrownBy(() ->
            new FromRoutePrefixSupplier("datasource-name", keywords)
        );
  }
}
