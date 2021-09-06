package collaborate.api.tag.model.storage;


import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.tag.model.TagEntry;
import java.util.List;
import java.util.Map;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

class IndexerQueryResponseTest {

  @Test
  void getFirst_shouldReturnEmptyMap_withMissingKey() {
    // GIVEN
    var queryIdxResponse = new IndexerQueryResponse<Long>();
    // WHEN
    var actual = queryIdxResponse.getFirstEntryValue("missing");
    // THEN
    assertThat(actual).isEmpty();
  }

  @Test
  void getFirst_shouldReturnEmptyMap_withKeyHavingNullValue() {
    // GIVEN
    var queryIdxResponse = new IndexerQueryResponse<Long>();
    String existingKey = "key";
    queryIdxResponse.put(existingKey, null);
    // WHEN
    var actual = queryIdxResponse.getFirstEntryValue(existingKey);
    // THEN
    assertThat(actual).isEmpty();
  }

  @Test
  void getFirst_shouldReturnEmptyMap_withKeyHavingEmptyValue() {
    // GIVEN
    var queryIdxResponse = new IndexerQueryResponse<Long>();
    String existingKey = "key";
    queryIdxResponse.put(existingKey, Lists.emptyList());
    // WHEN
    var actual = queryIdxResponse.getFirstEntryValue(existingKey);
    // THEN
    assertThat(actual).isEmpty();
  }

  @Test
  void getFirst_shouldReturnExpectedMap_withKeyHavingValue() {
    // GIVEN
    var queryIdxResponse = new IndexerQueryResponse<Long>();
    String existingKey = "key";
    Map<String, Long> expectedValue = Map.of("0", 1L);
    queryIdxResponse
        .put(existingKey, List.of(
            TagEntry.<String, Map<String, Long>>builder()
                .key("v")
                .value(expectedValue)
                .build()
        ));
    // WHEN
    var actual = queryIdxResponse.getFirstEntryValue(existingKey);

    // THEN
    assertThat(actual).containsExactlyInAnyOrderEntriesOf(expectedValue);
  }
}
