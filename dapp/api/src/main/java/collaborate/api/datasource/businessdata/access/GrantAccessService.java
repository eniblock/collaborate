package collaborate.api.datasource.businessdata.access;

import static java.lang.String.format;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.businessdata.NftScopeService;
import collaborate.api.datasource.businessdata.access.model.AccessRequestParams;
import collaborate.api.datasource.businessdata.access.model.PendingAccessRequest.Id;
import collaborate.api.datasource.model.dto.web.authentication.Authentication;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantAccessService {

  private final AuthenticationService authenticationService;
  private final GrantTransferMethodVisitorFactory grantTransferMethodVisitorFactory;
  private final NftScopeService nftScopeService;
  private final PendingAccessRequestRepository pendingAccessRequestRepository;
  private final ObjectMapper objectMapper;

  public void grant(Transaction transaction) {
    AccessRequestParams accessRequestParams = getAccessRequestParams(transaction);
    var nftId = accessRequestParams.getNftId();
    var nftScope = nftScopeService.findOneByNftId(nftId)
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
      OAuth2ClientCredentialsGrant clientCredentialsGrant) {
    var nftScope = nftScopeService.findOneByNftId(nftId)
        .orElseThrow(() -> new IllegalStateException("Scope not found for nftId=" + nftId));

    var requesterAuthorization = authenticationService.saveRequesterClientCredentials(
        businessDataContractAddress,
        requesterAddress,
        nftId, clientCredentialsGrant);

    var pendingAccessRquests = pendingAccessRequestRepository
        .findById(new Id(requesterAddress, nftId));
    if (pendingAccessRquests.isPresent()) {
      requesterAuthorization.getPartnerTransferMethod().accept(
          grantTransferMethodVisitorFactory.create(nftScope, requesterAddress)
      );
    }
  }
}
