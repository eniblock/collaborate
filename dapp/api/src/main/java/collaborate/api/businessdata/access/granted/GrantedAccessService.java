package collaborate.api.businessdata.access.granted;

import collaborate.api.businessdata.access.CipherJwtService;
import collaborate.api.businessdata.access.grant.GrantAccessDAO;
import collaborate.api.businessdata.access.grant.model.AccessGrantParams;
import collaborate.api.tag.model.user.UserMetadataDTO;
import collaborate.api.transaction.Transaction;
import collaborate.api.user.UserService;
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

  private final CipherJwtService cipherService;
  private final GrantAccessDAO grantAccessDAO;
  private final ObjectMapper objectMapper;
  private final UserService userService;
  private final TezosApiGatewayUserClient tagUserClient;

  public void storeJwtToken(Transaction transaction) {
    log.info("New grant_access transaction=={}", transaction);
    var accessGrantParams = getAccessGrantParams(transaction);

    String decipheredJWT = cipherService.decipher(accessGrantParams.getCipheredToken());
    storeJWT(accessGrantParams, decipheredJWT);
    log.info("Credentials has been stored");
  }

  private void storeJWT(AccessGrantParams accessGrantParams, String decipheredJWT) {
    var accessRequest = grantAccessDAO.findOneById(accessGrantParams.getAccessRequestsUuid())
        .orElseThrow((() -> new NotFoundException(
            "accessRequest" + accessGrantParams.getAccessRequestsUuid())));
    var scope = accessRequest.getScopes().stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("No scope in accessRequest" + accessRequest));
    var user = userService.createUser(scope);
    tagUserClient.upsertMetadata(user.getUserId(), new UserMetadataDTO(decipheredJWT));
  }

  private AccessGrantParams getAccessGrantParams(Transaction transaction) {
    try {
      return objectMapper.treeToValue(transaction.getParameters(), AccessGrantParams.class);
    } catch (JsonProcessingException e) {
      log.error(
          "While converting transactionParameters={} to AccessRequestParams",
          transaction.getParameters()
      );
      throw new IllegalStateException(e);
    }
  }

}
