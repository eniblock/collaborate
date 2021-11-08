package collaborate.api.businessdata.access.grant;

import collaborate.api.businessdata.access.grant.model.AccessGrantParams;
import collaborate.api.businessdata.access.request.model.AccessRequestParams;
import collaborate.api.datasource.OAuth2JWTProvider;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.organization.OrganizationService;
import collaborate.api.security.CipherService;
import collaborate.api.transaction.Transaction;
import collaborate.api.user.tag.TezosApiGatewayUserClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.InvalidKeyException;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccessGrantService {

  private final CipherService cipherService;
  private final OAuth2JWTProvider oAuth2JWTProvider;
  private final OrganizationService organizationService;
  private final ObjectMapper objectMapper;
  private final TezosApiGatewayUserClient tagUserClient;
  private final GrantAccessDAO grantAccessDAO;

  public void addAccessGrant(Transaction transaction) {
    AccessRequestParams accessRequestParams = getAccessRequestParams(transaction);
    // Get OAuth2 vault metadata
    VaultMetadata vaultMetadata = getVaultMetadata(accessRequestParams);

    // Get the accessRequest Key:
    var currentOrganization = organizationService.getCurrentOrganization();
    var accessRequest = grantAccessDAO.findOneByRequesterAndProviderAndNft(
        accessRequestParams,
        transaction.getSource(),
        currentOrganization.getAddress()
    ).orElseThrow(() -> new IllegalStateException("requestAccess not found"));
    // Get JWT
    var accessTokenResponse = oAuth2JWTProvider.get(
        vaultMetadata.getOAuth2(),
        // FIXME we should not handle a list of scope
        Optional.of(accessRequestParams.getScopes().get(0))
    );

    // Cipher token
    var accessGrantParams = buildAccessGrantParams(
        accessRequest.getKey(),
        accessTokenResponse.getAccessToken()
    );
    grantAccessDAO.grantAccess(accessGrantParams);
  }

  private VaultMetadata getVaultMetadata(AccessRequestParams accessRequestParams) {
    var datasourceId = StringUtils.substringBefore(accessRequestParams.getScopes().get(0), ":");
    var vaultMetadataResponse = tagUserClient.getMetadata(datasourceId);
    VaultMetadata vaultMetadata;
    try {
      vaultMetadata = objectMapper.readValue(
          vaultMetadataResponse.getBody().getData(),
          VaultMetadata.class
      );
    } catch (JsonProcessingException e) {
      log.error("while converting vaultMetadata");
      throw new IllegalStateException(e);
    }
    if (vaultMetadata.getOAuth2() == null) {
      log.error("Access request for datasourceId={} received but oAuth2 metadata seems to be empty",
          datasourceId);
      throw new NotImplementedException();
    }
    return vaultMetadata;
  }

  private AccessGrantParams buildAccessGrantParams(UUID uuid,
      String accessToken) {
    try {
      return AccessGrantParams.builder()
          .id(uuid)
          .jwtToken(cipherToken(accessToken))
          .build();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new IllegalStateException(e);
    }
  }

  private String cipherToken(String accessToken)
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    var currentOrganization = organizationService.getCurrentOrganization();
    return cipherService.cipher(accessToken, currentOrganization.getEncryptionKey());
  }

  private AccessRequestParams getAccessRequestParams(Transaction transaction) {
    AccessRequestParams accessRequestParams;
    try {
      accessRequestParams = objectMapper.treeToValue(transaction.getParameters(),
          AccessRequestParams.class);
    } catch (JsonProcessingException e) {
      log.error("While converting transactionParameters={} to AccessRequestParams",
          transaction.getParameters());
      throw new IllegalStateException(e);
    }
    return accessRequestParams;
  }

}
