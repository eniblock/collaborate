package collaborate.api.tag.config;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.TestResources;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TagConfigTest {

  private final TagConfig expectedTagConfig = new TagConfig(
      Set.of("https://ithacanet.ecadinfra.com"),
      Set.of(
          new TezosIndexer("tzstats", "https://api.ithaca.tzstats.com/"),
          new TezosIndexer("tzkt", "https://api.ithacanet.tzkt.io/")
      )
  );

  @Test
  void deserialize_shouldResultInExpectedObject() {
    // GIVEN

    // WHEN
    var actualTagConfig = TestResources.readContent(
        "/tag/config/tag-config-response.json",
        TagConfig.class
    );
    // THEN
    assertThat(actualTagConfig).isNotNull();
    assertThat(actualTagConfig).isEqualTo(expectedTagConfig);
  }

  @Test
  void findIndexerUrlByName_shouldReturnExistingIndexerUrl() {
    // GIVEN

    // WHEN
    var actualUrlOpt = expectedTagConfig.findIndexerUrlByName("tzstats");
    // THEN
    assertThat(actualUrlOpt).hasValue(
        new TezosIndexer("tzstats", "https://api.ithaca.tzstats.com/")
    );
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
