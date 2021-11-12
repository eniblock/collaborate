package collaborate.api.gateway;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.AccessTokenProvider;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.user.metadata.UserMetadataService;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayUrlService {

  private final GatewayUrlDAO gatewayURLDAO;
  private final TraefikProperties traefikProperties;
  private final UserMetadataService userMetadataService;
  private final AccessTokenProvider accessTokenProvider;

  public ResponseEntity<JsonNode> fetch(GatewayResourceDTO resourceDTO,
      Optional<AccessTokenResponse> accessTokenOpt) {
    var uriBuilder = UriComponentsBuilder.fromUriString(traefikProperties.getUrl())
        .path("/datasource")
        .path("/" + resourceDTO.getDatasourceId())
        .path("/" + resourceDTO.getScope());

    if (isNotBlank(resourceDTO.getAssetIdForDatasource())) {
      uriBuilder.path("/" + resourceDTO.getAssetIdForDatasource());
    }
    if (accessTokenOpt.isEmpty()) {
      accessTokenOpt = findOAuth2Jwt(resourceDTO.getDatasourceId());
    }
    var uri = uriBuilder.build().toUriString();
    return gatewayURLDAO.fetch(uri, accessTokenOpt);
  }

  private Optional<AccessTokenResponse> findOAuth2Jwt(String datasourceId) {
    var vaultMetadataO = userMetadataService.find(
        datasourceId,
        VaultMetadata.class
    );
    if (vaultMetadataO.isPresent()) {
      var oAuth2 = vaultMetadataO.get().getOAuth2();
      if (oAuth2 != null) {
        return Optional.of(accessTokenProvider.get(oAuth2, Optional.empty()));
      }
    }
    return Optional.empty();
  }


}
