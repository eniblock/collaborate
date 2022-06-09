package collaborate.api.user.metadata;

import static collaborate.api.datasource.businessdata.document.AssetsService.ASSET_ID_SEPARATOR;

import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.user.CleanUserService;
import collaborate.api.user.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserMetadataService {

  private final CleanUserService cleanUserService;
  private final TagUserMetadataDAO tagUserMetadataDAO;
  private final UserService userService;

  public <T> Optional<T> find(String userId, Class<T> tClass) {
    userId = cleanUserService.cleanUserId(userId);
    return tagUserMetadataDAO.getMetadata(userId, tClass);
  }

  public void upsertMetadata(String scope, Object metadata) {
    var user = userService.createUser(scope);
    var userId = user.getUserId();
    userId = cleanUserService.cleanUserId(userId);
    tagUserMetadataDAO.upsertMetadata(userId, metadata);
  }

  public Optional<OAuth2ClientCredentialsGrant> getOwnerOAuth2(String datasourceId) {
    return find(datasourceId, VaultMetadata.class)
        .filter(VaultMetadata::hasOAuth2)
        .map(VaultMetadata::getOAuth2);
  }

  public Optional<String> getRequesterAccessToken(String datasourceId, String scope) {
    return find(datasourceId + ASSET_ID_SEPARATOR + scope, VaultMetadata.class)
        .filter(VaultMetadata::hasJwt)
        .map(VaultMetadata::getJwt);
  }
}
