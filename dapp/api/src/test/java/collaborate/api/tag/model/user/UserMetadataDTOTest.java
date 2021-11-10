package collaborate.api.tag.model.user;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.TestResources;
import org.junit.jupiter.api.Test;

class UserMetadataDTOTest {

  @Test
  void deserialize_shouldResultInExpectedObject() {
    // GIVEN
    // WHEN
    var userMetadataResult = TestResources.readContent(
        "/tag/model/user/post-metadata-response.json",
        UserMetadataDTO.class
    );
    // THEN
    assertThat(userMetadataResult.getData()).isEqualTo("myTestData");
  }
}
