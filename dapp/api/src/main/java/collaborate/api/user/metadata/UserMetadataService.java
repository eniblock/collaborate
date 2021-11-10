package collaborate.api.user.metadata;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserMetadataService {

  private final TagUserMetadataDAO tagUserMetadataDAO;

  public <T> Optional<T> findMetadata(String userId, Class<T> tClass) {
    return tagUserMetadataDAO.getMetadata(userId, tClass);
  }

  public void upsertMetadata(String userId, Object metadata) {
    tagUserMetadataDAO.upsertMetadata(userId, metadata);
  }
}
