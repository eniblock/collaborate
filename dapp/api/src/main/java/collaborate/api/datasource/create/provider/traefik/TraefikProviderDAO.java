package collaborate.api.datasource.create.provider.traefik;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.config.api.YamlMapper;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * Write datasource configuration in the Data API Gateway
 */
@Repository
@Slf4j
@RequiredArgsConstructor
class TraefikProviderDAO {

  private final YamlMapper yamlMapper;
  private final TraefikProperties traefikProperties;

  public void save(TraefikProviderConfiguration providerConfiguration, String datasourceId) {
    File outputFile = getPath(datasourceId).toFile();
    try {
      yamlMapper.writeValue(outputFile, providerConfiguration);
    } catch (IOException e) {
      log.error("Can't write datasource provider configuration", e);
      throw new IllegalStateException(e);
    }
  }

  public Path getPath(String datasourceId) {
    return Paths.get(
        traefikProperties.getProvidersPath(),
        datasourceId + ".yml"
    );
  }

  public boolean exists(String datasourceId) {
    return getPath(datasourceId).toFile().exists();
  }

}
