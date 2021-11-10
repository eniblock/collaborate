package collaborate.api.user.metadata;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserMetadataService {

  private final TagUserMetadataDAO tagUserMetadataDAO;

  public <T> T getMetadata(String userId, Class<T> tClass) {
    return tagUserMetadataDAO.getMetadata(userId, tClass);
  }

  public void upsertMetadata(String userId, Object metadata) {
    tagUserMetadataDAO.upsertMetadata(userId, metadata);
  }
}
