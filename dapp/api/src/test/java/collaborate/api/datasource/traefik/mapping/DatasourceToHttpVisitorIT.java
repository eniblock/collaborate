package collaborate.api.datasource.traefik.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import collaborate.api.datasource.domain.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.traefik.domain.EntryPoint;
import collaborate.api.traefik.domain.HttpFeatures;
import collaborate.api.traefik.mapping.MiddlewareFactory;
import collaborate.api.traefik.mapping.RouterFactory;
import collaborate.api.traefik.mapping.ServiceFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class DatasourceToHttpVisitorIT {

  private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
  private final MiddlewareFactory middlewareFactory = new MiddlewareFactory();
  private final RouterFactory routerFactory = new RouterFactory();
  private final ServiceFactory serviceFactory = new ServiceFactory();

  @Test
  void accept_shouldGenerateSerializableHttp() throws Exception {
    // GIVEN
    WebServerDatasource datasource = CertificateBasedBasicAuthDatasourceFeatures.datasource;
    var visitor = new DatasourceToHttpVisitor(
        "/ssl/certs/",
        middlewareFactory,
        routerFactory,
        serviceFactory
    );

    // WHEN
    datasource.accept(visitor);
    // THEN
    var serializedTraefikConfiguration = yamlMapper.writeValueAsString(new EntryPoint(visitor.getHttp()));
    System.out.println(serializedTraefikConfiguration);
    assertDoesNotThrow(() -> "No exception is thrown");
  }

  @Test
  void accept_shouldGenerateExpectedHttp() throws Exception {
    // GIVEN
    WebServerDatasource datasource = CertificateBasedBasicAuthDatasourceFeatures.datasource;
    var visitor = new DatasourceToHttpVisitor(
        "/ssl/certs/",
        middlewareFactory,
        routerFactory,
        serviceFactory
    );

    // WHEN
    datasource.accept(visitor);

    // THEN
    var actualHttp = visitor.getHttp();
    var expectedHttp = HttpFeatures.entryPoint.getHttp();
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
