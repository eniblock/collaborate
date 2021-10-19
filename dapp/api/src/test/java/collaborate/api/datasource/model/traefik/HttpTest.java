package collaborate.api.datasource.model.traefik;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class HttpTest {

  private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  @Test
  void serialize_shouldNotThrowException() throws IOException {
    // WHEN
    var actualResult = mapper.writeValueAsString(HttpFeatures.PROVIDER_CONFIGURATION);
    // THEN
    assertThat(actualResult).isNotNull();

  }

  @Test
  void serializeDeserialize_shouldBeIdentity() throws IOException {
    // GIVEN
    // WHEN
    var yaml = mapper.writeValueAsString(HttpFeatures.PROVIDER_CONFIGURATION);
    var entryPoint = mapper.readValue(yaml, TraefikProviderConfiguration.class);
    // THEN No exception is thrown
    assertThat(entryPoint).isEqualTo(HttpFeatures.PROVIDER_CONFIGURATION);
  }

}
