package collaborate.api.nft.model.metadata;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class TokenMetadataTest {

  @Test
  void getAttributeByName_shouldReturnsEmptyWithNullAttribute() {
    // GIVEN
    var tokenMetadata = new TokenMetadata();
    // WHEN
    var attributeResult = tokenMetadata.getAttributeByName("");
    // THEN
    assertThat(attributeResult).isNotPresent();
  }

  @Test
  void getAttributeByName_shouldReturnsExpectedAttributeWithValidExistingAttributeName() {
    // GIVEN
    Attribute expectedAttribute = new Attribute("name", "value", "type");
    var tokenMetadata = TokenMetadata.builder()
        .attributes(List.of(expectedAttribute))
        .build();
    // WHEN
    var attributeResult = tokenMetadata.getAttributeByName("name");
    // THEN
    assertThat(attributeResult).hasValue(expectedAttribute);
  }

  @Test
  void getAssetDataCatalogUri_shouldReturnEmptyWithUnexistingAttribute() {
    // GIVEN
    var tokenMetadata = new TokenMetadata();
    // WHEN
    var attributeResult = tokenMetadata.getAssetDataCatalogUri();
    // THEN
    assertThat(attributeResult).isNotPresent();
  }

  @Test
  void getAssetDataCatalogUri_shouldReturnExpectedWithExistingAttribute() {
    // GIVEN
    Attribute expectedAttribute = new Attribute("assetDataCatalog", "value", "type");
    var tokenMetadata = TokenMetadata.builder()
        .attributes(List.of(expectedAttribute))
        .build();
    // WHEN
    var attributeResult = tokenMetadata.getAssetDataCatalogUri();
    // THEN
    assertThat(attributeResult).hasValue("value");
  }
}
