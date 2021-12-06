package collaborate.api.user.metadata;

import static collaborate.api.datasource.businessdata.document.ScopeAssetsService.ASSET_ID_SEPARATOR;

import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
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
