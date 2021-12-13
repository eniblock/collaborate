package collaborate.api.datasource.passport.create;

import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.tag.model.proxytokencontroller.MultisigBuildParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

class InitPassportCreationEntryPointParamTest {

  @Test
  void deserialize_shouldBeExpectedTagParam()
      throws JsonProcessingException {
    // GIVEN
    var multisiBuildParam = CreateFeatures.initPassportCreationEntryPointParam;
    String toJson = objectMapper.writeValueAsString(multisiBuildParam)
        .replace(" ", "");
    // WHEN
    var actual = CreateFeatures.initPassportCreationParamJson
        .replace(" ", "")
        .replace("\n", "");
    // THEN
    assertThat(actual).isEqualTo(toJson);
  }

}
