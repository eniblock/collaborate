package collaborate.api.datasource.gateway;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.businessdata.NftService;
import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.DatasourceRepository;
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
  private final DatasourceRepository datasourceRepository;
  private final TraefikProviderService traefikProviderService;

  public ResponseEntity<String> fetch(GatewayResourceDTO resourceDTO) {
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
    var datasource = datasourceRepository.findById(resourceDTO.getDatasourceId())
      .stream()
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("datasource not found for id =" + resourceDTO.getDatasourceId()));
      
    String baseURI = traefikProviderService.buildDatasourceBaseUri(datasource);
    log.debug(baseURI);
    var uriBuilder = UriComponentsBuilder.fromUriString(baseURI) // traefikProperties.getUrl()
        .path("/datasource")
        .path("/" + resourceDTO.getDatasourceId())
        .path("/" + resourceDTO.getAlias());

    if (isNotBlank(resourceDTO.getAssetIdForDatasource())) {
      uriBuilder.path("/" + resourceDTO.getAssetIdForDatasource());
    }
    return uriBuilder.build().toUriString();
  }

}
