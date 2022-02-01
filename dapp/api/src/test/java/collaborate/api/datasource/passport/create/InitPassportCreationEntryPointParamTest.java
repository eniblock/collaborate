package collaborate.api.datasource.passport.create;

import static collaborate.api.test.TestResources.objectMapper;
import static collaborate.api.test.TestResources.readValue;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.tag.model.proxytokencontroller.MultisigBuildCallParamMint;
import collaborate.api.tag.model.proxytokencontroller.MultisigBuildParam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

class InitPassportCreationEntryPointParamTest {

  @Test
  void deserializeThenSerialize_shouldBeIdentity() throws JsonProcessingException {
    // GIVEN
    var multisiBuildParam = CreateFeatures.initPassportCreationEntryPointParam;

    // WHEN
    String serializedResult = objectMapper.writeValueAsString(multisiBuildParam);
    var deserializedResult = readValue(serializedResult,
        new TypeReference<MultisigBuildParam<MultisigBuildCallParamMint>>() {
        });
    // THEN
    assertThat(deserializedResult).isEqualTo(multisiBuildParam);
  }

}
