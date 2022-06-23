package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.businessdata.access.model.AccessGrantParams;
import collaborate.api.datasource.businessdata.access.model.AccessRequest;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.transaction.Transaction;
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

  private final String businessDataContractAddress;
  private final CipherJwtService cipherService;
  private final GrantAccessDAO grantAccessDAO;
  private final ObjectMapper objectMapper;
  private final UserMetadataService userMetadataService;
  private final NftDatasourceService nftDatasourceService;


  public void onGrantedAccess(Transaction transaction) {
    log.info("New grant_access transaction={}", transaction);
    var accessGrantParams = getAccessGrantParams(transaction);
    var accessRequest = grantAccessDAO.findOneAccessRequestById(
            accessGrantParams.getAccessRequestsUuid())
        .orElseThrow((() -> new NotFoundException(
            "accessRequest with uuid=" + accessGrantParams.getAccessRequestsUuid())));

    String decipheredJWT = cipherService.decipher(accessGrantParams.getCipheredToken());
    storeJWT(accessRequest, decipheredJWT);

    nftDatasourceService.saveConfigurationByTokenId(
        accessRequest.getTokenId(),
        businessDataContractAddress
    );

    log.info("Credentials has been stored");
  }

  private void storeJWT(AccessRequest accessRequest, String decipheredJWT) {
    var scope = accessRequest.getScopes()
        .stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No scope in accessRequest=" + accessRequest));
    userMetadataService.upsertMetadata(
        scope,
        VaultMetadata.builder().jwt(decipheredJWT).build()
    );
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
