package collaborate.api.businessdata.access.granted;

import collaborate.api.businessdata.access.grant.GrantAccessDAO;
import collaborate.api.businessdata.access.grant.model.AccessGrantParams;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.security.CipherService;
import collaborate.api.tag.model.user.UserMetadataDTO;
import collaborate.api.transaction.Transaction;
import collaborate.api.user.tag.TezosApiGatewayUserClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantedAccessService {

  private final ApiProperties apiProperties;
  private final CipherService cipherService;
  private final GrantAccessDAO grantAccessDAO;
  private final ObjectMapper objectMapper;
  private final TezosApiGatewayUserClient tagUserClient;

  public void storeJwtToken(Transaction transaction) throws JsonProcessingException {
    var accessGrantParams = getAccessGrantParams(transaction);

    String decipheredJWT;
    try {
      decipheredJWT = cipherService.decipher(
          accessGrantParams.getJwtToken(),
          apiProperties.getPrivateKey()
      );
    } catch (Exception e) {
      log.error("can't decipher transaction JWT {}", transaction);
      throw new IllegalStateException(e);
    }

    storeJWT(accessGrantParams, decipheredJWT);
  }

  private void storeJWT(AccessGrantParams accessGrantParams, String decipheredJWT) {
    // Get the scope
    var accessRequest = grantAccessDAO.findOneById(accessGrantParams.getId())
        .orElseThrow((() -> new NotFoundException("accessRequest" + accessGrantParams.getId())));
    var scope = accessRequest.getScopes().stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("No scope in accessRequest" + accessRequest));

    // store the key for the given scopeId
    tagUserClient.upsertMetadata(scope, new UserMetadataDTO(decipheredJWT));
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

}
