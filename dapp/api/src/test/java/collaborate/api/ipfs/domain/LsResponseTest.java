package collaborate.api.ipfs.domain;

import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.ipfs.IpfsFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

public class LsResponseTest {

  @Test
  void lsResponse_shouldBeDeserializable() throws JsonProcessingException {
    // GIVEN
    String ipfsLsResponseJson = IpfsFeature.ipfsLsResponseJson;
    // WHEN
    LsResponse lsResponse = objectMapper.readValue(ipfsLsResponseJson, LsResponse.class);
    // THEN
    assertThat(lsResponse).isEqualTo(IpfsFeature.ipfsLsResponse);
  }
}
