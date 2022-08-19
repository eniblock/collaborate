package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.businessdata.access.model.AccessGrantParams;
import collaborate.api.datasource.businessdata.access.model.AccessRequestParams;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.nft.AssetScopeRepository;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantAccessService {

  private final AssetScopeRepository assetScopeRepository;
  private final AccessTokenProvider accessTokenProvider;
  private final AuthenticationService authenticationService;
  private final CipherJwtService cipherService;
  private final ObjectMapper objectMapper;
  private final GrantAccessDAO grantAccessDAO;

  public void grant(Transaction transaction) {
    AccessRequestParams accessRequestParams = getAccessRequestParams(transaction);
    var nftId = accessRequestParams.getNftId();
    var assetScope = assetScopeRepository.findOneByNftId(nftId)
        .orElseThrow(() -> new IllegalStateException("Scope not found for nftId=" + nftId));

    authenticationService
        .getAuthentication(assetScope.getAssetScopeId().getDatasource())
        .getPartnerTransferMethod()
        .accept(new AccessTokenTransferMethodVisitor(
            accessTokenProvider,
            authenticationService,
            assetScope.getAssetScopeId().getDatasource(),
            assetScope.getScope(),
            buildSendAccessGrantedTransactionConsumer(transaction.getSource())
        ));
  }

  public Consumer<String> buildSendAccessGrantedTransactionConsumer(String requester) {
    return accessToken -> {
      // Cipher token
      var accessGrantParams = toAccessGrantParams(
          accessToken,
          requester
      );
      log.debug("accessGrantParams={}", accessGrantParams);
      grantAccessDAO.grantAccess(accessGrantParams);
    };
  }

  AccessGrantParams toAccessGrantParams(String accessToken, String requester) {
    try {
      return AccessGrantParams.builder()
          .requesterAddress(requester)
          .cipheredToken(cipherService.cipher(accessToken, requester))
          .build();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new IllegalStateException(e);
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

}
