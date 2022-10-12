package collaborate.api.datasource.businessdata.access;

import static java.lang.String.format;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.businessdata.NftService;
import collaborate.api.datasource.businessdata.access.model.AccessRequestParams;
import collaborate.api.datasource.businessdata.access.model.ClientIdAndSecret;
import collaborate.api.datasource.businessdata.access.model.PendingAccessRequest.Id;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.model.dto.web.authentication.Authentication;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantAccessService {

  private final AccessTokenProvider accessTokenProvider;
  private final AuthenticationService authenticationService;
  private final CipherJwtService cipherJwtService;
  private final GrantAccessDAO grantAccessDAO;
  private final GrantTransferMethodVisitorFactory grantTransferMethodVisitorFactory;
  private final NftService nftService;
  private final PendingAccessRequestRepository pendingAccessRequestRepository;
  private final ObjectMapper objectMapper;

  public void grant(Transaction transaction) {
    AccessRequestParams accessRequestParams = getAccessRequestParams(transaction);
    var nftId = accessRequestParams.getNftId();
    var nftScope = nftService.findOneByNftId(nftId)
        .orElseThrow(() -> new IllegalStateException("Scope not found for nftId=" + nftId));

    var transferMethodOpt = authenticationService
        .findAuthentication(nftScope.getDatasourceId())
        .map(Authentication::getPartnerTransferMethod);
    if (transferMethodOpt.isPresent()) {
      transferMethodOpt.get().accept(
          grantTransferMethodVisitorFactory.create(nftScope, transaction.getSource())
      );
    } else {
      throw new IllegalStateException(format(
          "Missing authentication for accessRequestParams=%s, nftScope=%s",
          accessRequestParams,
          nftScope
      ));
    }
  }

  public AccessRequestParams getAccessRequestParams(Transaction transaction) {
    try {
      return objectMapper.treeToValue(
          transaction.getParameters(),
          AccessRequestParams.class
      );
    } catch (JsonProcessingException e) {
      log.error(
          "While converting transactionParameters={} to AccessRequestParams",
          transaction.getParameters());
      throw new IllegalStateException(e);
    }
  }

  /**
   * Manual grant, for an example when using e-mail notification transfer method
   */
  public void grant(String businessDataContractAddress, String requesterAddress, Integer nftId,
      ClientIdAndSecret clientCredentialsGrant) {
    var nftScope = nftService.findOneByNftId(nftId)
        .orElseThrow(() -> new IllegalStateException("Scope not found for nftId=" + nftId));

    var auth = authenticationService.findAuthentication(nftScope.getDatasourceId());
    if (auth.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "No datasource found for nft" + nftId);
    }
    var ownerAuth = (OAuth2ClientCredentialsGrant) auth.get();
    var requesterAuth = OAuth2ClientCredentialsGrant.builder()
        .tokenEndpoint(ownerAuth.getTokenEndpoint())
        .clientId(clientCredentialsGrant.getClientId())
        .clientSecret(clientCredentialsGrant.getClientSecret())
        .grantType(ownerAuth.getGrantType())
        .build();

    authenticationService.saveCredentials(
        businessDataContractAddress,
        requesterAddress,
        nftId,
        requesterAuth
    );

    var pendingAccessRquests = pendingAccessRequestRepository
        .findById(new Id(requesterAddress, nftId));
    if (pendingAccessRquests.isPresent()) {
      log.debug("Granting {}", pendingAccessRquests.get());
      var accessTokenResponse = accessTokenProvider.get(
          requesterAuth,
          nftScope.findScope()
      );
      var cipheredToken = cipherJwtService.cipher(accessTokenResponse.getAccessToken(),
          requesterAddress);
      grantAccessDAO.grantAccess(cipheredToken, requesterAddress, nftScope.getNftId());
      pendingAccessRequestRepository.delete(pendingAccessRquests.get());
    } else {
      log.debug("No pending access requests");
    }
  }
}
