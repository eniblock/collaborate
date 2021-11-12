package collaborate.api.businessdata.document;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.SCOPE_ASSET_LIST;
import static java.util.stream.Collectors.toList;

import collaborate.api.businessdata.document.model.ScopeAssetDTO;
import collaborate.api.businessdata.document.model.ScopeAssetsDTO;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.AccessTokenProvider;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.gateway.GatewayResourceDTO;
import collaborate.api.gateway.GatewayUrlService;
import collaborate.api.nft.find.TokenMetadataService;
import collaborate.api.passport.model.AssetDataCatalogDTO;
import collaborate.api.user.metadata.UserMetadataService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
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

  public static final String ASSET_ID_SEPARATOR = ":";
  private final AccessTokenProvider accessTokenProvider;
  private final ApiProperties apiProperties;
  private final Clock clock;
  private final GatewayUrlService gatewayUrlService;
  private final UserMetadataService userMetadataService;
  private final TokenMetadataService tokenMetadataService;

  public Optional<ScopeAssetsDTO> listScopeAssets(Integer tokenId) {
    var catalogOpt = tokenMetadataService.findByTokenId(
        tokenId,
        apiProperties.getBusinessDataContractAddress()
    );
    return catalogOpt
        .map(AssetDataCatalogDTO::getDatasources)
        .stream()
        .flatMap(Collection::stream)
        .map(d -> listScopeAssets(d.getId(), d.getAssetIdForDatasource()))
        .findFirst();
  }


  public ScopeAssetsDTO listScopeAssets(String datasourceId, String scope) {
    var jwtO = getJwt(datasourceId, scope);
    if (jwtO.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    var scopesResponse = getAssetListResponse(datasourceId, scope, jwtO.get());
    if (!scopesResponse.getStatusCode().is2xxSuccessful()) {
      log.error("Can't get asset list for datasourceId={} and scope={}", datasourceId, scope);
      throw new ResponseStatusException(scopesResponse.getStatusCode());
    }
    String assetListJsonString = scopesResponse.getBody().toString();
    return ScopeAssetsDTO.builder()
        .datasourceId(datasourceId)
        .scopeName(scope)
        .assets(filterByScope(assetListJsonString, scope).collect(toList()))
        .build();
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
        .scope(SCOPE_ASSET_LIST)
        .build();
    return gatewayUrlService.fetch(
        gatewayResource,
        Optional.of(accessToken)
    );
  }

  Stream<ScopeAssetDTO> filterByScope(String jsonResponse, String scope) {
    var resourcesPath = JSONPath.compile("$._embedded.metadatas");
    var namePath = JSONPath.compile("$.title");
    var linkPath = JSONPath.compile("$._links.self.href");
    var downloadPath = JSONPath.compile("$._links.download.href");
    var scopePath = JSONPath.compile("$.scope");
    if (resourcesPath.contains(jsonResponse)) {
      var docs = resourcesPath.<JSONArray>eval(jsonResponse, JSONArray.class);
      return docs.stream()
          .filter(scopePath::contains)
          .filter(d -> scope.equals(scopePath.eval(d, String.class)))
          .map(d -> ScopeAssetDTO.builder()
              .name(namePath.eval(d, String.class))
              .type("")
              .synchronizedDate(ZonedDateTime.now(clock))
              .link(URI.create(linkPath.eval(d, String.class)))
              .downloadLink(URI.create(downloadPath.eval(d, String.class)))
              .build());
    }
    return Stream.empty();
  }
}
