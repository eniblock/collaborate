package collaborate.api.datasource.traefik.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.gateway.traefik.RouterFactory;
import collaborate.api.datasource.gateway.traefik.model.Router;
import java.util.List;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

class RouterFactoryTest {

  RouterFactory routerFactory = new RouterFactory();

  @Test
  void create_shouldExpectedPathPrefix() {
    // GIVEN
    // WHEN
    var actualRouter = routerFactory.create("prefix", "service", Lists.emptyList(), false);
    // THEN
    assertThat(actualRouter).isEqualTo(
        Router.builder()
            .entryPoints(List.of("websecure"))
            .rule("PathPrefix(`prefix`)")
            .service("service")
            .middlewares(Lists.emptyList())
            .tls(false)
            .build()
    );
  }
}
