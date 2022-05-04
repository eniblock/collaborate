package collaborate.api.datasource.gateway.datasource.traefik.routing;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.gateway.traefik.routing.FromRoutePrefixSupplier;
import collaborate.api.datasource.model.dto.web.Attribute;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FromRoutePrefixSupplierTest {

  public static final String DATASOURCE_NAME = "datasource-name";

  private static Stream<Arguments> getParameters() {
    return Stream.of(
        Arguments.of(List.of(Attribute.builder()
                .name("provider:routing:alias")
                .value("odometer").build()),
            "/datasource/datasource-name/odometer"),
        Arguments.of(List.of(Attribute.builder().name("list-asset").build()),
            "/datasource/datasource-name/list-asset")
    );
  }

  @ParameterizedTest
  @MethodSource("getParameters")
  void get(Collection<Attribute> keywords, String expectedRoutePrefix) {
    // GIVEN
    var supplier = new FromRoutePrefixSupplier(DATASOURCE_NAME, keywords);
    // WHEN
    var routePrefixResult = supplier.get();
    // THEN
    assertThat(routePrefixResult).isEqualTo(expectedRoutePrefix);
  }

}
