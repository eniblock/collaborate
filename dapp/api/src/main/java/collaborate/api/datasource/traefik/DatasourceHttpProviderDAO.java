package collaborate.api.datasource.traefik;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.config.api.YamlMapper;
import collaborate.api.datasource.domain.Datasource;
import collaborate.api.datasource.traefik.mapping.DatasourceToHttpVisitor;
import collaborate.api.traefik.domain.EntryPoint;
import collaborate.api.traefik.domain.Http;
import collaborate.api.traefik.mapping.MiddlewareFactory;
import collaborate.api.traefik.mapping.RouterFactory;
import collaborate.api.traefik.mapping.ServiceFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DatasourceHttpProviderDAO {

  private final ApiProperties apiProperties;
  private final YamlMapper yamlMapper;
  private final MiddlewareFactory middlewareFactory;
  private final RouterFactory routerFactory;
  private final ServiceFactory serviceFactory;

  public void save(Datasource datasource) throws Exception {
    var datasourceToHttpVisitor = new DatasourceToHttpVisitor(
        apiProperties.getTraefik().getCertificatesPath().toString(),
        middlewareFactory,
        routerFactory,
        serviceFactory
    );
    datasource.accept(datasourceToHttpVisitor);
    Path ouput = Paths.get(apiProperties.getTraefik().getProvidersPath().toString(),
        datasource.getId().toString() + ".yml");
    write(ouput, datasourceToHttpVisitor.getHttp());
  }

  private void write(Path output, Http http)
      throws IOException {
    var providerConfiguration = yamlMapper.writeValueAsString(new EntryPoint(http));
    log.debug("Writing datasource provider configuration path={{}}, content={}", output,
        providerConfiguration);
    Files.writeString(output, providerConfiguration);
  }
}
