package collaborate.api.datasource.traefik.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FromRoutePrefixSupplierTest {

  public static final String DATASOURCE_NAME = "datasource-name";

  private static Stream<Arguments> getParameters() {
    return Stream.of(
        Arguments.of("scope:odometer", "/datasource/datasource-name/scope:odometer"),
        Arguments.of("scope:metric:odometer", "/datasource/datasource-name/scope:metric:odometer"),
        Arguments.of("document:test", "/datasource/datasource-name/document:test")
    );
  }

  @ParameterizedTest
  @MethodSource("getParameters")
  void get(String scope, String expectedRoutePrefix) {
    // GIVEN
    var supplier = new FromRoutePrefixSupplier(DATASOURCE_NAME, Set.of(scope));
    // WHEN
    var routePrefixResult = supplier.get();
    // THEN
    assertThat(routePrefixResult).isEqualTo(expectedRoutePrefix);
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
