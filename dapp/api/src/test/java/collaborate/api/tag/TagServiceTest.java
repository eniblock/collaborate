package collaborate.api.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import collaborate.api.tag.config.TagConfigFeature;
import collaborate.api.tag.config.TezosApiGatewayConfClient;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

  @Mock
  TagStorageClient tagStorageClient;
  @Mock
  TezosApiGatewayConfClient tezosApiGatewayConfClient;
  @InjectMocks
  TagService tagService;

  String contractAddress = "contractAddress";
  JsonNode tagResponse = TestResources.readContent(
      "/tag/tag-storage-field.response.json",
      JsonNode.class);

  @Test
  void getBigmapIdByName_shouldBeEmpty_withTagErrorResponse() {
    // GIVEN
    String fieldName = "not_existing";
    when(tagStorageClient.getFields(eq(contractAddress), any()))
        .thenReturn(tagResponse);
    // WHEN
    var actualResult = tagService.findBigmapIdByName(fieldName, contractAddress);
    // THEN
    assertThat(actualResult).isEmpty();
  }

  @Test
  void getBigmapIdByName_shouldBeEmpty_withFieldNotBigmap() {
    // GIVEN
    String fieldName = "all_tokens";
    when(tagStorageClient.getFields(eq(contractAddress), any()))
        .thenReturn(tagResponse);
    // WHEN
    var actualResult = tagService.findBigmapIdByName(fieldName, contractAddress);
    // THEN
    assertThat(actualResult).isEmpty();
  }

  @Test
  void getBigmapIdByName_shouldBeExpectedInteger() {
    // GIVEN
    String fieldName = "token_id_by_asset_id";
    when(tagStorageClient.getFields(eq(contractAddress), any()))
        .thenReturn(tagResponse);
    // WHEN
    var actualResult = tagService.findBigmapIdByName(fieldName, contractAddress);
    // THEN
    assertThat(actualResult).hasValue(21736);
  }

  @Test
  void findTokenMetadataTzStatsUrl_shouldBeExpected() {
    // GIVEN
    when(tezosApiGatewayConfClient.getConfig()).thenReturn(TagConfigFeature.TAG_CONFIG);
    when(tagStorageClient.getFields(eq(contractAddress), any()))
        .thenReturn(tagResponse);
    // WHEN
    var actualResult = tagService.findTokenMetadataTzktUrl(contractAddress);
    // THEN
    assertThat(actualResult).hasValue(
        "https://ithacanet.tzkt.io/" + contractAddress + "/storage/21735");
  }
}
