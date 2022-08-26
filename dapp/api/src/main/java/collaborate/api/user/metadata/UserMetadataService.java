package collaborate.api.user.metadata;

import static collaborate.api.datasource.businessdata.document.AssetsService.ASSET_ID_SEPARATOR;

import collaborate.api.datasource.model.VaultDatasourceAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.user.CleanVaultUserService;
import collaborate.api.user.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserMetadataService {

  private final CleanVaultUserService cleanVaultUserService;
  private final TagUserMetadataDAO tagUserMetadataDAO;
  private final UserService userService;

  public <T> Optional<T> find(String userId, Class<T> tClass) {
    userId = cleanVaultUserService.cleanUserId(userId);
    return tagUserMetadataDAO.getMetadata(userId, tClass);
  }

  public void upsertMetadata(String scope, Object metadata) {
    var user = userService.createUser(scope);
    var userId = user.getUserId();
    userId = cleanVaultUserService.cleanUserId(userId);
    tagUserMetadataDAO.upsertMetadata(userId, metadata);
  }

  public Optional<OAuth2ClientCredentialsGrant> getOwnerOAuth2(String datasourceId) {
    // TODO COL-552
    return null;
//    return find(datasourceId, VaultDatasourceMetadata.class)
//        .filter(VaultDatasourceMetadata::hasOAuth2)
//        .map(VaultDatasourceMetadata::getOAuth2);
  }

  public Optional<String> getRequesterAccessToken(String datasourceId, String scope) {
    return find(datasourceId + ASSET_ID_SEPARATOR + scope, VaultDatasourceAuth.class)
        .filter(VaultDatasourceAuth::hasJwt)
        .map(VaultDatasourceAuth::getJwt);
  }
}
