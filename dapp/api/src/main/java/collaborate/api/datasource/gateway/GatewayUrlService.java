package collaborate.api.datasource.gateway;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.model.AssetScope;
import collaborate.api.datasource.model.AssetScopeId;
import collaborate.api.datasource.nft.AssetScopeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayUrlService {

  private final AssetScopeRepository assetScopeRepository;
  private final AuthenticationService authenticationService;
  private final GatewayUrlDAO gatewayURLDAO;
  private final TraefikProperties traefikProperties;

  public ResponseEntity<JsonNode> fetch(GatewayResourceDTO resourceDTO) {
    String uri = buildURL(resourceDTO);

    var aliasScopeOpt = assetScopeRepository
        .findById(new AssetScopeId(resourceDTO.getDatasourceId(), resourceDTO.getAlias()))
        .map(AssetScope::getScope);

    var bearerOpt = authenticationService.findAuthorizationHeader(
        resourceDTO.getDatasourceId(),
        aliasScopeOpt);

    return gatewayURLDAO.fetch(uri, bearerOpt);
  }

  String buildURL(GatewayResourceDTO resourceDTO) {
    var uriBuilder = UriComponentsBuilder.fromUriString(traefikProperties.getUrl())
        .path("/datasource")
        .path("/" + resourceDTO.getDatasourceId())
        .path("/" + resourceDTO.getAlias());

    if (isNotBlank(resourceDTO.getAssetIdForDatasource())) {
      uriBuilder.path("/" + resourceDTO.getAssetIdForDatasource());
    }
    var uri = uriBuilder.build().toUriString();
    return uri;
  }

}
