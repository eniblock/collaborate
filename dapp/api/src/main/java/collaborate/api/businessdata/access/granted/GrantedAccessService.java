package collaborate.api.businessdata.access.granted;

import collaborate.api.businessdata.access.grant.model.AccessGrantParams;
import collaborate.api.security.CipherService;
import collaborate.api.transaction.Transaction;
import collaborate.api.user.tag.TezosApiGatewayUserClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.PrivateKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantedAccessService {

  private final CipherService cipherService;
  private final ObjectMapper objectMapper;
  private final TezosApiGatewayUserClient tagUserClient;

  public void storeJwtToken(Transaction transaction) {
    var accessGrantParams = getAccessGrantParams(transaction);

    // decipher the key
    var jwt = uncipherJwt(accessGrantParams.getJwtToken());
    // store the key for the given scopeId

    // new UserMetadataDTO(objectMapper.writeValueAsString())
    //tagUserClient.upsertMetadata(accessGrantParams.getScopeId(), )
  }

  private AccessGrantParams getAccessGrantParams(Transaction transaction) {
    try {
      return objectMapper.treeToValue(transaction.getParameters(),
          AccessGrantParams.class);
    } catch (JsonProcessingException e) {
      log.error("While converting transactionParameters={} to AccessRequestParams",
          transaction.getParameters());
      throw new IllegalStateException(e);
    }
  }

  String uncipherJwt(String cipheredJwt) {
    // FIXME
    String privateKeyAsString = "";
    PrivateKey privateKey = CipherService.getPrivateKey(privateKeyAsString);

    try {
      return cipherService.decipher(cipheredJwt, privateKey);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return privateKeyAsString;
  }
}
