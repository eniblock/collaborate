package collaborate.api.user.metadata;

import collaborate.api.user.CleanUserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserMetadataService {

  private final TagUserMetadataDAO tagUserMetadataDAO;
  private final CleanUserService cleanUserService;

  public <T> Optional<T> find(String userId, Class<T> tClass) {
    userId = cleanUserService.cleanUserId(userId);
    return tagUserMetadataDAO.getMetadata(userId, tClass);
  }

  public void upsertMetadata(String userId, Object metadata) {
    userId = cleanUserService.cleanUserId(userId);
    tagUserMetadataDAO.upsertMetadata(userId, metadata);
  }
}
