package collaborate.api.businessdata.access;

import collaborate.api.businessdata.access.model.AccessGrantParams;
import collaborate.api.businessdata.access.model.AccessRequestParams;
import collaborate.api.datasource.OAuth2JWTProvider;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.transaction.Transaction;
import collaborate.api.user.metadata.UserMetadataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
import javax.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccessGrantService {

  private final CipherJwtService cipherService;
  private final OAuth2JWTProvider oAuth2JWTProvider;
  private final ObjectMapper objectMapper;
  private final UserMetadataService userMetadataService;
  private final GrantAccessDAO grantAccessDAO;

  public void grant(Transaction transaction) {
    AccessRequestParams accessRequestParams = getAccessRequestParams(transaction);
    var requester = transaction.getSource();
    // Get OAuth2 vault metadata
    VaultMetadata vaultMetadata = getVaultMetadata(accessRequestParams);

    // Get JWT
    var accessTokenResponse = oAuth2JWTProvider.get(
        vaultMetadata.getOAuth2(),
        Optional.of(accessRequestParams.getDatasourceScope())
    );

    // Cipher token
    var accessGrantParams = toAccessGrantParams(
        accessRequestParams.getAccessRequestsUuid(),
        accessTokenResponse.getAccessToken(),
        requester
    );
    log.info("accessGrantParams={}", accessGrantParams);
    grantAccessDAO.grantAccess(accessGrantParams);
  }

  private VaultMetadata getVaultMetadata(AccessRequestParams accessRequestParams) {
    var datasourceId = accessRequestParams.getDatasourceId();
    return userMetadataService
        .findMetadata(datasourceId, VaultMetadata.class)
        .filter(m -> m.getOAuth2() != null)
        .orElseThrow(() -> {
          log.error(
              "Access request for datasourceId={} received but oAuth2 metadata seems to be missing",
              datasourceId);
          throw new NotFoundException();
        });
  }

  private AccessGrantParams toAccessGrantParams(UUID uuid, String accessToken, String requester) {
    try {
      return AccessGrantParams.builder()
          .accessRequestsUuid(uuid)
          .requesterAddress(requester)
          .cipheredToken(cipherService.cipher(accessToken, requester))
          .build();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new IllegalStateException(e);
    }
  }

  AccessRequestParams getAccessRequestParams(Transaction transaction) {
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
