package collaborate.api.datasource;

import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

class DatasourceTest {

  @Test
  void deserialize_canTypeProvider() throws ClassNotFoundException {
    // GIVEN
    String datasourceJson = TestResources.readContent("/datasource/domain/web/datasource.json");
    // WHEN

    Datasource genericDatasourceResult = TestResources.readValue(
        datasourceJson,
        new TypeReference<>() {
        }
    );
    var providerClass = Class.forName(genericDatasourceResult.getProvider());
    Object rawProviderConfiguration = objectMapper
        .convertValue(genericDatasourceResult.getProviderConfiguration(), providerClass);

    // THEN
    assertThat(rawProviderConfiguration).isInstanceOf(TraefikProviderConfiguration.class);
  }
}
