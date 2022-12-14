package collaborate.api.datasource.traefik.mapping;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.gateway.traefik.RouterFactory;
import collaborate.api.datasource.gateway.traefik.model.Router;
import java.util.List;
import org.junit.jupiter.api.Test;

class RouterFactoryTest {

  RouterFactory routerFactory = new RouterFactory();

  @Test
  void create_shouldExpectedPathPrefix() {
    // GIVEN
    // WHEN
    var actualRouter = routerFactory.create("prefix", "service", emptyList(), false);
    // THEN
    assertThat(actualRouter).isEqualTo(
        Router.builder()
            .entryPoints(List.of("websecure"))
            .rule("PathPrefix(`prefix`)")
            .service("service")
            .middlewares(emptyList())
            .tls(false)
            .build()
    );
  }
}
