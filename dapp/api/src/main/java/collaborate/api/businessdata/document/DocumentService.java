package collaborate.api.businessdata.document;

import static collaborate.api.businessdata.document.AssetIdValidator.ASSET_ID_SEPARATOR;

import collaborate.api.datasource.AccessTokenProvider;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.gateway.GatewayResourceDTO;
import collaborate.api.gateway.GatewayUrlService;
import collaborate.api.user.metadata.UserMetadataService;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@Service
public class DocumentService {

  private final GatewayUrlService gatewayUrlService;
  private final UserMetadataService userMetadataService;
  private final AccessTokenProvider accessTokenProvider;

  public ResponseEntity<JsonNode> listAssetDocuments(String datasourceId, String scope) {
    var jwtO = getJwt(datasourceId, scope);
    if (jwtO.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var scopesResponse = getAssetListResponse(datasourceId, scope, jwtO.get());
    if (!scopesResponse.getStatusCode().is2xxSuccessful()) {
      log.error("Can't get asset list for datasourceId={} and scope={}", datasourceId, scope);
      throw new ResponseStatusException(scopesResponse.getStatusCode());
    }
    return scopesResponse;
  }

  Optional<AccessTokenResponse> getJwt(String datasourceId, String scope) {
    var oAuthScope = StringUtils.removeStart(scope, "scope:");
    return getOAuth2(datasourceId)
        .map(oAuth2 -> getOwnerAccessToken(oAuth2, oAuthScope))
        .or(() -> getRequesterAccessToken(datasourceId, oAuthScope));
  }

  Optional<OAuth2> getOAuth2(String datasourceId) {
    return userMetadataService.find(datasourceId, VaultMetadata.class)
        .filter(VaultMetadata::hasOAuth2)
        .map(VaultMetadata::getOAuth2);
  }

  private AccessTokenResponse getOwnerAccessToken(OAuth2 auth2, String scope) {
    return accessTokenProvider.get(auth2, Optional.of(scope));
  }

  private Optional<AccessTokenResponse> getRequesterAccessToken(String datasourceId, String scope) {
    return userMetadataService
        .find(datasourceId + ASSET_ID_SEPARATOR + scope, VaultMetadata.class)
        .filter(VaultMetadata::hasJwt)
        .map(VaultMetadata::getJwt)
        .map(accessToken -> AccessTokenResponse.builder().accessToken(accessToken).build());
  }

  ResponseEntity<JsonNode> getAssetListResponse(String datasourceId,
      String scope,
      AccessTokenResponse accessToken) {
    var gatewayResource = GatewayResourceDTO.builder()
        .datasourceId(datasourceId)
        .scope(scope)
        .build();
    return gatewayUrlService.fetch(
        gatewayResource,
        Optional.of(accessToken)
    );
  }

}
