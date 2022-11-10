package collaborate.api.datasource.servicedata.access;

import static java.lang.String.format;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.businessdata.NftService;
import collaborate.api.datasource.servicedata.access.model.AccessRequestParams;
import collaborate.api.datasource.businessdata.access.model.ClientIdAndSecret;
import collaborate.api.datasource.businessdata.access.PendingAccessRequestRepository;
import collaborate.api.datasource.businessdata.access.CipherJwtService;
import collaborate.api.datasource.businessdata.access.model.PendingAccessRequest.Id;
import collaborate.api.datasource.nft.TokenDAO;
import collaborate.api.datasource.servicedata.model.ServiceData;
import collaborate.api.ipfs.IpfsService;
import collaborate.api.tag.model.TokenMetadata;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.model.dto.web.authentication.Authentication;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantSubscribeService {

  private final AccessTokenProvider accessTokenProvider;
  private final AuthenticationService authenticationService;
  private final CipherJwtService cipherJwtService;
  private final GrantSubscribeDAO grantAccessDAO;
  private final GrantSubscribeTransferMethodVisitorFactory grantTransferMethodVisitorFactory;
  private final NftService nftService;
  private final PendingAccessRequestRepository pendingAccessRequestRepository;
  private final ObjectMapper objectMapper;
  private final String serviceDataContractAddress;
  private final IpfsService ipfsService;  
  private final TokenDAO tokenMetadataDAO;

  public void grant(Transaction transaction) {
    AccessRequestParams accessRequestParams = getAccessRequestParams(transaction);
    var tokenId = accessRequestParams.getNftId();

    var tokenMedataOpt = tokenMetadataDAO.findById(tokenId, serviceDataContractAddress);
    var serviceData = tokenMedataOpt
            .map(TokenMetadata::getIpfsUri)
            .flatMap(this::findByIpfsLink)
            .orElseThrow(() -> new IllegalStateException(
                format("No metadata found for nftId=%d, smartContract=%s", tokenId, serviceDataContractAddress)
            ));

     List<String> scopes = serviceData.getServices().stream()
        .map(r -> { return r.getValue(); })
        .collect(Collectors.toList());

    var scope = scopes.get(0); //nftId = ids.get(0); // TODO: iterate

    var nftScope = nftService.findById(scope.split(":")[0], scope.split(":")[1]) //nftService.findOneByNftId(nftId)
        .orElseThrow(() -> new IllegalStateException("Scope not found for nftId=" + scope));

    log.info("findScope: "+nftScope.findScope()+" id:"+nftScope.getNftId());

    var transferMethodOpt = authenticationService
        .findAuthentication(nftScope.getDatasourceId())
        .map(Authentication::getPartnerTransferMethod);
        
    if (transferMethodOpt.isPresent()) {
      transferMethodOpt.get().accept(
          grantTransferMethodVisitorFactory.create(nftScope, transaction.getSource())
      );
    } else {
      throw new IllegalStateException(format(
          "Missing authentication for accessRequestParams=%s", //, nftScope=%s",
          accessRequestParams
          //nftScope
      ));
    }
  }

  public Optional<ServiceData> findByIpfsLink(String tZip21Url) {
    try {
      var tokenMetadata = ipfsService.cat(tZip21Url, ServiceData.class);
      return Optional.ofNullable(tokenMetadata);
    } catch (Exception e) {
      log.error("While getting data from tZip21Url={}\n{}", tZip21Url, e);
      return Optional.empty();
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
  
  public void grant(String serviceDataContractAddress, String requesterAddress, Integer nftId, ClientIdAndSecret clientCredentialsGrant) {
    
    var nftScope = nftService.findOneByNftId(nftId).orElseThrow(() -> new IllegalStateException("Scope not found for nftId=" + nftId));

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
        serviceDataContractAddress,
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
   */
}
