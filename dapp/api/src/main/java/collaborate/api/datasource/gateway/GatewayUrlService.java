package collaborate.api.datasource.gateway;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.businessdata.NftService;
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

  private final NftService nftService;
  private final AuthenticationService authenticationService;
  private final GatewayUrlDAO gatewayURLDAO;
  private final TraefikProperties traefikProperties;

  public ResponseEntity<JsonNode> fetch(GatewayResourceDTO resourceDTO) {
    String uri = buildURL(resourceDTO);

    Optional<String> bearerOpt = nftService.findById(
        resourceDTO.getDatasourceId(),
        resourceDTO.getAlias()
    ).flatMap(nftScope -> authenticationService.findAuthorizationHeader(
        resourceDTO.getDatasourceId(),
        nftScope)
    );

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
    return uriBuilder.build().toUriString();
  }

}
