package collaborate.api.datasource;

import static java.lang.String.format;

import collaborate.api.datasource.businessdata.NftScopeService;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.model.AccessMethodNameVisitor;
import collaborate.api.datasource.model.NftScope;
import collaborate.api.datasource.model.VaultDatasourceAuth;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.Authentication;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.model.dto.web.authentication.transfer.OAuth2SharedCredentials;
import collaborate.api.datasource.model.dto.web.authentication.transfer.PartnerTransferMethod;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.http.HttpClientFactory;
import collaborate.api.user.metadata.UserMetadataService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

  private final AccessTokenProvider accessTokenProvider;
  private final NftScopeService nftScopeService;
  private final AccessMethodNameVisitor accessMethodNameVisitor;
  private final CatalogService catalogService;
  private final HttpClientFactory httpClientFactory;
  private final DatasourceRepository datasourceRepository;
  private final UserMetadataService userMetadataService;

  public void save(DatasourceDTO datasourceDTO) {
    userMetadataService.upsertMetadata(
        datasourceDTO.getId().toString(),
        VaultDatasourceAuth.builder()
            .authentication(datasourceDTO.getAuthMethod())
            .build()
    );
  }

  public Authentication getAuthentication(String datasourceId) {
    return userMetadataService.find(datasourceId, VaultDatasourceAuth.class)
        .map(VaultDatasourceAuth::getAuthentication).orElseThrow(() -> new IllegalStateException(
            format("Datasource id=%s not found in vault", datasourceId))
        );
  }

  public String getAccessMethodName(String datasourceId) {
    return getAuthentication(datasourceId).accept(accessMethodNameVisitor);
  }

  public PartnerTransferMethod getTransferMethod(String datasourceId) {
    return getAuthentication(datasourceId).getPartnerTransferMethod();
  }

  // TODO COL-557 Remove ?
  @Deprecated
  public Optional<String> findAuthorizationHeader(String datasourceId, Optional<String> scope) {
    var datasourceOpt = datasourceRepository.findById(datasourceId);
    if (datasourceOpt.isPresent()) {
      var bearer = getAuthentication(datasourceId).accept(
          new AuthenticationBearerVisitor(httpClientFactory, scope)
      );
      return Optional.ofNullable(bearer);
    } else {
      var vaultKey = buildVaultKey(datasourceId, scope);
      return userMetadataService.find(vaultKey, VaultDatasourceAuth.class)
          .map(VaultDatasourceAuth::getJwt)
          .map(jwt -> "Bearer " + jwt);
    }
  }

  public String getJwt(Integer nftId, String smartContract) {
    Optional<String> nftJWTOpt = findRequestedJWT(nftId, smartContract);
    if (nftJWTOpt.isEmpty()) {
      nftJWTOpt = findOwnerJWT(nftId, smartContract);
    }
    return nftJWTOpt.orElseThrow(
        () -> new ResponseStatusException(HttpStatus.PROXY_AUTHENTICATION_REQUIRED));
  }

  private Optional<String> findOwnerJWT(Integer nftId, String smartContract) {
    var datasourceIdOpt = catalogService.getCatalogByTokenId(nftId, smartContract)
        .getDatasources().stream()
        .findFirst()
        .map(AssetDetailsDatasourceDTO::getId);
    if (datasourceIdOpt.isPresent()) {
      var datasourceId = datasourceIdOpt.get();
      var scopeOpt = nftScopeService.findOneByNftId(nftId)
          .map(NftScope::getScope);
      return userMetadataService
          .find(datasourceId, VaultDatasourceAuth.class)
          .map(datasourceAuth -> accessTokenProvider.get(
              (OAuth2ClientCredentialsGrant) datasourceAuth.getAuthentication(), scopeOpt)
          ).map(AccessTokenResponse::getAccessToken);
    }
    return Optional.empty();
  }

  private Optional<String> findRequestedJWT(Integer nftId, String smartContract) {
    // FIXME COL-557
    return userMetadataService
        .find(nftId.toString(), VaultDatasourceAuth.class)
        .map(VaultDatasourceAuth::getJwt);
  }


  String buildVaultKey(String datasourceId, Optional<String> scope) {
    return datasourceId + ":" + scope.orElse("");
  }

  public void saveRequesterClientCredentials(Integer nftId, String contractAddress,
      String decipheredJWT) {
    userMetadataService.upsertMetadata(
        buildSharedCredentialsNftKey(contractAddress, nftId),
        VaultDatasourceAuth.builder()
            .jwt(decipheredJWT)
            .build()
    );
  }

  public OAuth2ClientCredentialsGrant saveRequesterClientCredentials(String contractAddress,
      String requesterAddress, Integer nftId, OAuth2ClientCredentialsGrant clientCredentialsGrant) {
    var authentication = OAuth2ClientCredentialsGrant.builder()
        .grantType(clientCredentialsGrant.getGrantType())
        .clientId(clientCredentialsGrant.getClientId())
        .clientSecret(clientCredentialsGrant.getClientSecret())
        .partnerTransferMethod(new OAuth2SharedCredentials())
        .build();

    userMetadataService.upsertMetadata(
        buildDedicatedCredentialsNftKey(contractAddress, requesterAddress, nftId),
        VaultDatasourceAuth.builder()
            .authentication(authentication)
            .build()
    );
    return authentication;
  }

  public String buildSharedCredentialsNftKey(String contractAddress, Integer nftId) {
    return contractAddress + ":" + nftId;
  }

  public String buildDedicatedCredentialsNftKey(String contractAddress,
      String requester, Integer nftId) {
    return contractAddress + "." + requester + "." + nftId;
  }

  public boolean isGranted(String datasourceId, Integer nftId, String smartContract) {
    return datasourceRepository.findById(datasourceId).isPresent()
        || userMetadataService.find(
        buildSharedCredentialsNftKey(smartContract, nftId),
        VaultDatasourceAuth.class
    ).isPresent();
  }


}
