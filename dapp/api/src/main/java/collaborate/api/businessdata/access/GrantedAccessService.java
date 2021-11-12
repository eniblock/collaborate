package collaborate.api.businessdata.access;

import collaborate.api.businessdata.access.model.AccessGrantParams;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.transaction.Transaction;
import collaborate.api.user.UserService;
import collaborate.api.user.metadata.UserMetadataService;
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
  private final UserMetadataService userMetadataService;

  public void storeJwtToken(Transaction transaction) {
    log.info("New grant_access transaction=={}", transaction);
    var accessGrantParams = getAccessGrantParams(transaction);

    String decipheredJWT = cipherService.decipher(accessGrantParams.getCipheredToken());
    storeJWT(accessGrantParams, decipheredJWT);
    log.info("Credentials has been stored");
  }

  private void storeJWT(AccessGrantParams accessGrantParams, String decipheredJWT) {
    var accessRequest = grantAccessDAO.findOneAccessRequestById(
            accessGrantParams.getAccessRequestsUuid())
        .orElseThrow((() -> new NotFoundException(
            "accessRequest" + accessGrantParams.getAccessRequestsUuid())));
    var scope = accessRequest.getScopes().stream().findFirst()
        .orElseThrow(() -> new IllegalStateException("No scope in accessRequest" + accessRequest));
    var user = userService.createUser(scope);

    userMetadataService.upsertMetadata(user.getUserId(),
        VaultMetadata.builder().jwt(decipheredJWT).build());
  }

  AccessGrantParams getAccessGrantParams(Transaction transaction) {
    try {
      return objectMapper.treeToValue(transaction.getParameters(), AccessGrantParams.class);
    } catch (JsonProcessingException e) {
      log.error(
          "While converting transactionParameters={} to AccessGrantParams",
          transaction.getParameters()
      );
      throw new IllegalStateException(e);
    }
  }

}
