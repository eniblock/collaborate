package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.businessdata.access.model.AccessGrantParams;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantedAccessService {

  private final String businessDataContractAddress;
  private final CipherJwtService cipherService;
  private final ObjectMapper objectMapper;
  private final NftDatasourceService nftDatasourceService;
  private final AuthenticationService authenticationService;


  public void onGrantedAccess(Transaction transaction) {
    var accessGrantParams = getAccessGrantParams(transaction);

    String decipheredJWT = cipherService.decipher(accessGrantParams.getCipheredToken());
    storeJWT(accessGrantParams, decipheredJWT);

    nftDatasourceService.saveConfigurationByTokenId(
        accessGrantParams.getNftId(),
        businessDataContractAddress
    );

    log.info("Credentials has been stored");
  }

  private void storeJWT(AccessGrantParams accessGrantParams, String decipheredJWT) {
    authenticationService.saveGrantedJwt(
        accessGrantParams.getNftId(),
        businessDataContractAddress,
        decipheredJWT
    );
  }

  public AccessGrantParams getAccessGrantParams(Transaction transaction) {
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
