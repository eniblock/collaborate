package collaborate.api.datasource.gateway;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.SCOPE_ASSET_LIST;
import static collaborate.api.datasource.passport.metric.MetricService.SCOPE_METRIC_PREFIX;
import static org.apache.commons.lang.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.MetadataService;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.ipfs.domain.dto.ContentWithCid;
import collaborate.api.user.metadata.UserMetadataService;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatewayUrlService {

  private final DatasourceService datasourceService;
  private final GatewayUrlDAO gatewayURLDAO;
  private final TraefikProperties traefikProperties;
  private final UserMetadataService userMetadataService;
  private final MetadataService metadataService;
  private final AccessTokenProvider accessTokenProvider;

  public ResponseEntity<JsonNode> fetch(GatewayResourceDTO resourceDTO) {
    var uriBuilder = UriComponentsBuilder.fromUriString(traefikProperties.getUrl())
        .path("/datasource")
        .path("/" + resourceDTO.getDatasourceId())
        .path("/" + resourceDTO.getScope());

    if (isNotBlank(resourceDTO.getAssetIdForDatasource())) {
      uriBuilder.path("/" + resourceDTO.getAssetIdForDatasource());
    }

    var accessTokenOpt = findOAuth2Jwt(
        resourceDTO.getDatasourceId(),
        resourceDTO.getScope());
    var uri = uriBuilder.build().toUriString();
    return gatewayURLDAO.fetch(uri, accessTokenOpt);
  }

  private Optional<AccessTokenResponse> findOAuth2Jwt(String datasourceId, String scope) {
    var vaultMetadataO = userMetadataService.find(
        datasourceId,
        VaultMetadata.class
    );
    if (vaultMetadataO.isPresent()) {
      var oAuth2 = vaultMetadataO.get().getOAuth2();
      if (oAuth2 != null) {
        return Optional.of(accessTokenProvider.get(oAuth2, cleanScope(scope, datasourceId)));
      }
    }
    return Optional.empty();
  }

  private Optional<String> cleanScope(@Nullable String scope, @NonNull String datasourceId) {
    if (scope == null) {
      return Optional.empty();
    } else {
      if (SCOPE_ASSET_LIST.equals(scope)) {
        return datasourceService.findById(datasourceId)
            .map(ContentWithCid::getContent)
            .flatMap(metadataService::getAssetListScope);
      } else {
        return Optional.of(removeStart(scope, SCOPE_METRIC_PREFIX));
      }
    }
  }
}
