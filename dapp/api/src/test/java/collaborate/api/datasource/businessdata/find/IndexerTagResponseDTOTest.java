package collaborate.api.datasource.businessdata.find;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.TestResources;
import org.junit.jupiter.api.Test;

class IndexerTagResponseDTOTest {

  @Test
  void canBeDeserialized() {
    // WHEN
    var indexer = TestResources.readContent(
        "/datasource/businessdata/indexer-response.json",
        IndexerTagResponseDTO.class
    );

    // THEN
    assertThat(indexer.getPassportsIndexerByDsp()).hasSize(3);
  }

  @Test
  void streamTokenIndexes_hasExpectedSize() {
    // WHEN
    var indexer = TestResources.readContent(
        "/datasource/businessdata/indexer-response.json",
        IndexerTagResponseDTO.class
    );

    // THEN
    assertThat(indexer.streamTokenIndexes()).hasSize(2);
  }
}
