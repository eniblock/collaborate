package collaborate.api.datasource.gateway.traefik;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.create.DatasourceToHttpVisitor;
import collaborate.api.datasource.gateway.traefik.model.TraefikProviderConfiguration;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class TraefikProviderConfigurationFactory {

  private final TraefikProperties traefikProperties;
  private final MiddlewareFactory middlewareFactory;
  private final RouterFactory routerFactory;
  private final ServiceFactory serviceFactory;

  public TraefikProviderConfiguration create(DatasourceDTO datasource)
      throws DatasourceVisitorException {
    var datasourceToHttpVisitor = new DatasourceToHttpVisitor(
        traefikProperties.getCertificatesPath(),
        middlewareFactory,
        routerFactory,
        serviceFactory
    );
    return new TraefikProviderConfiguration(datasource.accept(datasourceToHttpVisitor));
  }
}
