package collaborate.api.businessdata.access;

import collaborate.api.businessdata.access.model.AccessGrantParams;
import collaborate.api.businessdata.access.model.AccessRequest;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.create.provider.traefik.TraefikProviderService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import collaborate.api.nft.find.TokenMetadataService;
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

  private final ApiProperties apiProperties;
  private final CipherJwtService cipherService;
  private final GrantAccessDAO grantAccessDAO;
  private final ObjectMapper objectMapper;
  private final TokenMetadataService tokenMetadataService;
  private final TraefikProviderService traefikProviderService;
  private final UserService userService;
  private final UserMetadataService userMetadataService;

  public void storeJwtToken(Transaction transaction) {
    log.info("New grant_access transaction=={}", transaction);
    var accessGrantParams = getAccessGrantParams(transaction);
    var accessRequest = grantAccessDAO.findOneAccessRequestById(
            accessGrantParams.getAccessRequestsUuid())
        .orElseThrow((() -> new NotFoundException(
            "accessRequest" + accessGrantParams.getAccessRequestsUuid())));

    String decipheredJWT = cipherService.decipher(accessGrantParams.getCipheredToken());
    storeJWT(accessRequest, decipheredJWT);
    storeDatasourceProviderConfiguration(accessRequest.getTokenId());
    log.info("Credentials has been stored");
  }

  private void storeDatasourceProviderConfiguration(Integer tokenId) {
    tokenMetadataService.getDatasourceProviderConfigurations(tokenId,
            apiProperties.getBusinessDataContractAddress())
        .forEach(this::storeDatasource);
  }

  private void storeDatasource(Datasource datasource) {
    if (!datasource.getProvider().equals(TraefikProviderConfiguration.class.getName())) {
      throw new IllegalStateException(
          "Invalid datasource provider type:" + datasource.getProvider());
    }
    var traefikConfiguration = objectMapper.convertValue(
        datasource.getProviderConfiguration(),
        TraefikProviderConfiguration.class);
    traefikProviderService.save(traefikConfiguration, datasource.getId());
  }

  private void storeJWT(AccessRequest accessRequest, String decipheredJWT) {
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
