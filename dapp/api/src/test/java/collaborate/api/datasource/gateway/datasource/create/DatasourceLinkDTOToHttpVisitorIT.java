package collaborate.api.datasource.gateway.datasource.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import collaborate.api.datasource.create.DatasourceToHttpVisitor;
import collaborate.api.datasource.gateway.datasource.traefik.model.HttpFeatures;
import collaborate.api.datasource.gateway.traefik.MiddlewareFactory;
import collaborate.api.datasource.gateway.traefik.RouterFactory;
import collaborate.api.datasource.gateway.traefik.ServiceFactory;
import collaborate.api.datasource.gateway.traefik.model.TraefikProviderConfiguration;
import collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class DatasourceLinkDTOToHttpVisitorIT {

  private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
  private final MiddlewareFactory middlewareFactory = new MiddlewareFactory();
  private final RouterFactory routerFactory = new RouterFactory();
  private final ServiceFactory serviceFactory = new ServiceFactory();

  @Test
  void accept_shouldGenerateSerializableHttp() throws Exception {
    // GIVEN
    WebServerDatasourceDTO datasource = CertificateBasedBasicAuthDatasourceFeatures.datasource;
    var visitor = new DatasourceToHttpVisitor(
        "/ssl/certs/",
        middlewareFactory,
        routerFactory,
        serviceFactory
    );

    // WHEN
    var http = datasource.accept(visitor);
    // THEN
    var serializedTraefikConfiguration = yamlMapper
        .writeValueAsString(new TraefikProviderConfiguration(http));
    System.out.println(serializedTraefikConfiguration);
    assertDoesNotThrow(() -> "No exception is thrown");
  }

  @Test
  void accept_shouldGenerateExpectedHttp() throws Exception {
    // GIVEN
    WebServerDatasourceDTO datasource = CertificateBasedBasicAuthDatasourceFeatures.datasource;
    var visitor = new DatasourceToHttpVisitor(
        "/ssl/certs/",
        middlewareFactory,
        routerFactory,
        serviceFactory
    );

    // WHEN
    var actualHttp = datasource.accept(visitor);

    // THEN
    var expectedHttp = HttpFeatures.PROVIDER_CONFIGURATION.getHttp();
    assertThat(actualHttp.getServersTransports()).as("Checking serverTransports")
        .isEqualTo(expectedHttp.getServersTransports());
    assertThat(actualHttp.getRouters()).as("Checking routers")
        .isEqualTo(expectedHttp.getRouters());
    assertThat(actualHttp.getServices()).as("Checking services")
        .isEqualTo(expectedHttp.getServices());
    assertThat(actualHttp.getMiddlewares()).as("Checking middlewares")
        .isEqualTo(expectedHttp.getMiddlewares());
  }

}
