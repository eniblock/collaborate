package collaborate.api.datasource.passport.create;

import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

class InitPassportCreationEntryPointParamTest {

  @Test
  void deserialize_shouldBeExpectedTagParam()
      throws JsonProcessingException {
    // GIVEN
    String json = CreateFeatures.initPassportCreationParamJson;
    // WHEN
    var actual = objectMapper.readValue(json, InitPassportCreationEntryPointParam.class);
    // THEN
    assertThat(actual).isEqualTo(CreateFeatures.initPassportCreationEntryPointParam);
  }
}
