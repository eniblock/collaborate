package collaborate.api.tag.model.user;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.TestResources;
import org.junit.jupiter.api.Test;

public class UserMetadataDTOTest {

  @Test
  void deserialize_shouldResultInExpectedObject() {
    // GIVEN
    // WHEN
    var userMetadataResult = TestResources.readPath(
        "/tag/model/user/post-metadata-response.json",
        UserMetadataDTO.class
    );
    // THEN
    assertThat(userMetadataResult.getData()).isEqualTo("myTestData");
  }
}
