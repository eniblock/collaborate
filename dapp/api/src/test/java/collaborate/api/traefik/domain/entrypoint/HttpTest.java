package collaborate.api.traefik.domain.entrypoint;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.traefik.CustomCharacterEscapes;
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
    // FIXME Check if instruction is required
    mapper.getFactory().setCharacterEscapes(new CustomCharacterEscapes());
    // WHEN
    var actualResult = mapper.writeValueAsString(HttpFeatures.entryPoint);
    // THEN
    assertThat(actualResult).isNotNull();

  }

  @Test
  void serializeDeserialize_shouldBeIdentity() throws IOException {
    // GIVEN
    // WHEN
    var yaml = mapper.writeValueAsString(HttpFeatures.entryPoint);
    var entryPoint = mapper.readValue(yaml, EntryPoint.class);
    // THEN No exception is thrown
    assertThat(entryPoint).isEqualTo(HttpFeatures.entryPoint);
  }

}