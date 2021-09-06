package collaborate.api.user.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class userDTOTest {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void name() throws JsonProcessingException {
    // GIVEN
    var user = "{ \"createdTimestamp\":1606743707654 }";
    // WHEN
    var actualUserDTO = objectMapper.readValue(user, UserDTO.class);
    // THEN
    assertThat(actualUserDTO.getCreatedTimestamp()).isNotNull();
    // TODO Assert that createdTimestamp as expected instant value
  }
}
