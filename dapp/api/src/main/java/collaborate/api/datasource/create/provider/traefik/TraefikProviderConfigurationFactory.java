package collaborate.api.datasource.create.provider.traefik;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.create.DatasourceToHttpVisitor;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class TraefikProviderConfigurationFactory {

  private final TraefikProperties traefikProperties;
  private final MiddlewareFactory middlewareFactory;
  private final RouterFactory routerFactory;
  private final ServiceFactory serviceFactory;


  public TraefikProviderConfiguration create(DatasourceDTO datasource) throws Exception {
    var datasourceToHttpVisitor = new DatasourceToHttpVisitor(
        traefikProperties.getCertificatesPath(),
        middlewareFactory,
        routerFactory,
        serviceFactory
    );
    datasource.accept(datasourceToHttpVisitor);
    return new TraefikProviderConfiguration(datasourceToHttpVisitor.getHttp());
  }
}
