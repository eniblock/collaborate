package collaborate.api.passport.create;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.tag.model.Bytes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class BytesTest {

  ObjectMapper mapper = new ObjectMapper();

  @Test
  void serialize_shouldConvertToexpectedString() throws JsonProcessingException {
    // GIVEN
    var bytes = new Bytes("YAY9CUHUJNOCWRE1G".getBytes(StandardCharsets.UTF_8));
    String expected = "\"59 41 59 39 43 55 48 55 4a 4e 4f 43 57 52 45 31 47\"".replace(" ","");

    // WHEN
    var actual = mapper.writeValueAsString(bytes);
    // THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void deserialize_shouldConvertToExpectedString() throws JsonProcessingException {
    // GIVEN
    String bytesString = "\"59 41 59 39 43 55 48 55 4a 4e 4f 43 57 52 45 31 47\"".replace(" ","");
    var expected = new Bytes("YAY9CUHUJNOCWRE1G".getBytes(StandardCharsets.UTF_8));

    // WHEN
    var actual = mapper.readValue(bytesString, Bytes.class);
    // THEN
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void serializeThenDeserialize_shouldBeIdentity() throws JsonProcessingException {
    // GIVEN
    var bytes = new Bytes("YAY9CUHUJNOCWRE1G".getBytes(StandardCharsets.UTF_8));
    // WHEN
    var actual = mapper.readValue(mapper.writeValueAsString(bytes), Bytes.class);
    // THEN
    assertThat(actual).isEqualTo(bytes);
  }
}
