package collaborate.api.user.model;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.TestResources;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

class UserDTOTest {

  @Test
  void name() throws JsonProcessingException {
    // GIVEN
    var user = "{ \"createdTimestamp\":1606743707654 }";
    // WHEN
    var actualUserDTO = TestResources.objectMapper.readValue(user, UserDTO.class);
    // THEN
    assertThat(actualUserDTO.getCreatedTimestamp()).isNotNull();
    // TODO Assert that createdTimestamp as expected instant value
  }
}
