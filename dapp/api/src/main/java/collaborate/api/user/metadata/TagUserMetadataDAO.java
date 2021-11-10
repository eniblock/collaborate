package collaborate.api.user.metadata;

import collaborate.api.tag.model.user.UserMetadataDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
class TagUserMetadataDAO {

  private final ObjectMapper objectMapper;
  private final TagUserMetadataClient tagUserMetadataClient;

  public <T> T getMetadata(String userId, Class<T> tClass) {
    var wrappedMetadata = tagUserMetadataClient.getMetadata(userId);
    try {
      return objectMapper.readValue(wrappedMetadata.getData(), tClass);
    } catch (JsonProcessingException e) {
      log.error("Can't deserialize metadata for userId={}", userId);
      throw new IllegalStateException(e);
    }
  }

  public void upsertMetadata(String userId, Object metadata) {
    String serialized;
    try {
      serialized = objectMapper.writeValueAsString(metadata);
    } catch (JsonProcessingException e) {
      log.error("can't serialized vaultMetadata for userId={}", userId);
      throw new IllegalStateException("Can't write vaultMetadata");
    }
    tagUserMetadataClient.upsertMetadata(userId, new UserMetadataDTO(serialized));
  }
}
