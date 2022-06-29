package collaborate.api.tag.config;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.TestResources;
import org.junit.jupiter.api.Test;

class TagConfigTest {

  public final TagConfig expectedTagConfig = TagConfigFeature.TAG_CONFIG;

  @Test
  void deserialize_shouldResultInExpectedObject() {
    // GIVEN

    // WHEN
    var actualTagConfig = TestResources.readContent(
        "/tag/config/tag-config-response.json",
        TagConfig.class
    );
    // THEN
    assertThat(actualTagConfig).isNotNull()
        .isEqualTo(expectedTagConfig);
  }

  @Test
  void findIndexerUrlByName_shouldReturnExistingIndexerUrl() {
    // GIVEN

    // WHEN
    var actualUrlOpt = expectedTagConfig.findIndexerUrlByName("tzstats");
    // THEN
    assertThat(actualUrlOpt).hasValue("https://api.ithaca.tzstats.com/");
  }

  @Test
  void findIndexerUrlByName_shouldReturnEmpty_withUnknownName() {
    // GIVEN
    // WHEN
    var actualUrlOpt = expectedTagConfig.findIndexerUrlByName("not-existing");
    // THEN
    assertThat(actualUrlOpt).isEmpty();
  }
}
