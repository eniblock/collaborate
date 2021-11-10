package collaborate.api.gateway;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.OAuth2JWTProvider;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.user.metadata.UserMetadataService;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
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
  private final OAuth2JWTProvider oAuth2JWTProvider;

  public ResponseEntity<JsonNode> fetch(String apiGatewayTargetURL) {
    return gatewayURLDAO.fetch(apiGatewayTargetURL, Optional.empty());
  }

  public ResponseEntity<JsonNode> fetch(String datasourceId, HttpServletRequest request) {
    String apiGatewayTargetURL = replaceBaseUrl(request);
    return gatewayURLDAO.fetch(apiGatewayTargetURL, findOAuth2Jwt(datasourceId));
  }

  String replaceBaseUrl(HttpServletRequest request) {
    return UriComponentsBuilder.fromUriString(request.getRequestURI().replace(
        GatewayController.API_V_1_GATEWAY,
        traefikProperties.getUrl())).build().toUriString();
  }

  private Optional<AccessTokenResponse> findOAuth2Jwt(String datasourceId) {
    var vaultMetadataO = userMetadataService.findMetadata(
        datasourceId,
        VaultMetadata.class
    );
    if (vaultMetadataO.isPresent()) {
      var oAuth2 = vaultMetadataO.get().getOAuth2();
      if (oAuth2 != null) {
        return Optional.of(oAuth2JWTProvider.get(oAuth2, Optional.empty()));
      }
    }
    return Optional.empty();
  }
}
