package collaborate.api.datasource.gateway.datasource.traefik.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;

import collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier;
import collaborate.api.datasource.model.dto.web.Attribute;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RoutingKeySupplierTest {

  @Test
  void get_shouldThrowException_withoutKeywordHavingAliasName() {
    // GIVEN
    List<Attribute> keywords = List.of(Attribute.builder()
        .name("scope")
        .value("metric:odometer")
        .build());

    // THEN
    assertThrows(IllegalStateException.class, () -> {
      // WHEN
      new RoutingKeyFromKeywordSupplier(keywords).get();
    });
  }

  @Test
  void get_shouldReturnListAsset_withAssetList() {
    // GIVEN
    List<Attribute> keywords = List.of(Attribute.builder()
        .name("list-asset")
        .build());
    // WHEN
    var route = new RoutingKeyFromKeywordSupplier(keywords).get();
    // THEN
    assertThat(route).isEqualTo("list-asset");
  }

  @Test
  void get_shouldReturnFirstAlias_withMultipleAliases() {
    // GIVEN
    List<Attribute> keywords = List.of(Attribute.builder()
            .name("alias")
            .value("alias1")
            .build(),
        Attribute.builder()
            .name("alias")
            .value("alias2")
            .build()
    );
    // WHEN
    var route = new RoutingKeyFromKeywordSupplier(keywords).get();
    // THEN
    assertThat(route).isEqualTo("alias1");
  }

}
