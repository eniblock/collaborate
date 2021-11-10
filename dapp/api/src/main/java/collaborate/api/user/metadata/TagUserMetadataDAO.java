package collaborate.api.user.metadata;

import static java.util.Objects.requireNonNull;

import collaborate.api.tag.model.user.UserMetadataDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
class TagUserMetadataDAO {

  private final ObjectMapper objectMapper;
  private final TagUserMetadataClient tagUserMetadataClient;

  public <T> Optional<T> getMetadata(String userId, Class<T> tClass) {
    var responseEntity = tagUserMetadataClient.getMetadata(userId);
    if (!responseEntity.getStatusCode().is2xxSuccessful()) {
      return Optional.empty();
    }
    try {
      return Optional.of(
          objectMapper.readValue(requireNonNull(responseEntity.getBody()).getData(), tClass)
      );
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
