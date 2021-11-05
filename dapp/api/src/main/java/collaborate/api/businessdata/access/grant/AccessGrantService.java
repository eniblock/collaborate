package collaborate.api.businessdata.access.grant;

import collaborate.api.businessdata.access.grant.model.AccessGrantParams;
import collaborate.api.businessdata.access.request.model.AccessRequestParams;
import collaborate.api.datasource.OAuth2JWTProvider;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.security.CipherService;
import collaborate.api.transaction.Transaction;
import collaborate.api.user.tag.TezosApiGatewayUserClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
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
    // Get OAuth2 vaul metadata
    VaultMetadata vaultMetadata = getVaultMetadata(accessRequestParams);

    // Get JWT
    var accessTokenResponse = oAuth2JWTProvider.get(
        vaultMetadata.getOAuth2(),
        Optional.of(accessRequestParams.getScope())
    );

    // Cipher token
    var accessGrantParams = buildAccessGrantParams(accessRequestParams, accessTokenResponse);
    grantAccessDAO.grantAccess(accessGrantParams);
  }

  private AccessGrantParams buildAccessGrantParams(AccessRequestParams accessRequestParams,
      AccessTokenResponse accessTokenResponse) {
    try {
      var currentOrganization = organizationService.getCurrentOrganization();
      String cipheredToken = cipherToken(accessTokenResponse, currentOrganization);

      return AccessGrantParams.builder()
          .scopeId(accessRequestParams.getScope())
          .jwtToken(cipheredToken)
          .providerAddress(currentOrganization.getAddress())
          .requesterAddress(accessRequestParams.getRequesterAddress())
          .build();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new IllegalStateException(e);
    }
  }

  private String cipherToken(AccessTokenResponse accessTokenResponse,
      OrganizationDTO currentOrganization)
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    PublicKey publicKey = CipherService.getKey(currentOrganization.getEncryptionKey());
    return cipherService.cipher(accessTokenResponse.getAccessToken(), publicKey);
  }

  private VaultMetadata getVaultMetadata(AccessRequestParams accessRequestParams) {
    var datasourceId = accessRequestParams.getDatasourceId();
    var vaultMetadataResponse = tagUserClient.getMetadata(
        accessRequestParams.getDatasourceId());
    VaultMetadata vaultMetadata;
    try {
      vaultMetadata = objectMapper.readValue(vaultMetadataResponse.getBody().getData(),
          VaultMetadata.class);
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
