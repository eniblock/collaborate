package collaborate.api.datasource;

import collaborate.api.datasource.businessdata.NftScopeService;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.model.AccessMethodNameVisitor;
import collaborate.api.datasource.model.Nft;
import collaborate.api.datasource.model.VaultDatasourceAuth;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.Authentication;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
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

  public Optional<Authentication> findAuthentication(String datasourceId) {
    return userMetadataService.find(datasourceId, VaultDatasourceAuth.class)
        .map(VaultDatasourceAuth::getAuthentication);
  }

  public String getAccessMethodName(String datasourceId) {
    return findAuthentication(datasourceId)
        .map(auth -> auth.accept(accessMethodNameVisitor))
        .orElse("");
  }

  public PartnerTransferMethod getTransferMethod(String datasourceId) {
    return findAuthentication(datasourceId)
        .map(Authentication::getPartnerTransferMethod)
        .orElse(null);
  }

  public Optional<String> findAuthorizationHeader(String datasourceId, Nft nft) {
    var datasourceOpt = findAuthentication(datasourceId);
    if (datasourceOpt.isPresent()) {
      // Owner
      return findAuthentication(datasourceId)
          .map(auth -> auth.accept(
              new AuthenticationBearerVisitor(
                  httpClientFactory,
                  nft.findScope()
              )
          ));
    } else {
      // Requester
      // FIXME
      return findRequestedJWT(nft.getNftId(), null)
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
          .flatMap(Nft::findScope);
      return userMetadataService
          .find(datasourceId, VaultDatasourceAuth.class)
          .map(datasourceAuth -> accessTokenProvider.get(
              (OAuth2ClientCredentialsGrant) datasourceAuth.getAuthentication(), scopeOpt)
          ).map(AccessTokenResponse::getAccessToken);
    }
    return Optional.empty();
  }

  private Optional<String> findRequestedJWT(Integer nftId, String smartContract) {
    return userMetadataService
        .find(buildByNftKey(smartContract, nftId), VaultDatasourceAuth.class)
        .map(VaultDatasourceAuth::getJwt);
  }


  String buildVaultKey(String datasourceId, Optional<String> scope) {
    return datasourceId + ":" + scope.orElse("");
  }

  public void saveRequestedAccessToken(Integer nftId, String contractAddress,
      String decipheredJWT) {
    userMetadataService.upsertMetadata(
        buildByNftKey(contractAddress, nftId),
        VaultDatasourceAuth.builder()
            .jwt(decipheredJWT)
            .build()
    );
  }

  public void saveCredentials(String contractAddress,
      String requesterAddress, Integer nftId, OAuth2ClientCredentialsGrant clientCredentialsGrant) {

    userMetadataService.upsertMetadata(
        buildDedicatedCredentialsNftKey(contractAddress, requesterAddress, nftId),
        VaultDatasourceAuth.builder()
            .authentication(clientCredentialsGrant)
            .build()
    );
  }

  public String buildByNftKey(String contractAddress, Integer nftId) {
    // FIXME
    return nftId.toString();
  }

  public String buildDedicatedCredentialsNftKey(String contractAddress,
      String requester, Integer nftId) {
    return contractAddress + "." + requester + "." + nftId;
  }

  public boolean isGranted(String datasourceId, Integer nftId, String smartContract) {
    return datasourceRepository.findById(datasourceId).isPresent()
        || userMetadataService.find(
        buildByNftKey(smartContract, nftId),
        VaultDatasourceAuth.class
    ).isPresent();
  }


}
