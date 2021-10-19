package collaborate.api.datasource.create.provider.traefik;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.config.api.YamlMapper;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import java.io.File;
import java.io.IOException;
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
public class TraefikProviderDAO {

  private final YamlMapper yamlMapper;
  private final TraefikProperties traefikProperties;

  public void save(TraefikProviderConfiguration providerConfiguration, String datasourceId)
      throws IOException {
    File outputFile = Paths.get(
        traefikProperties.getProvidersPath(),
        datasourceId + ".yml"
    ).toFile();
    yamlMapper.writeValue(outputFile, providerConfiguration);
  }

}
