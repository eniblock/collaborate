package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.businessdata.access.model.AccessGrantParams;
import collaborate.api.datasource.businessdata.access.model.AccessRequestParams;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.kpi.Kpi;
import collaborate.api.datasource.kpi.KpiService;
import collaborate.api.datasource.model.dto.VaultMetadata;
import collaborate.api.datasource.model.scope.AssetScope;
import collaborate.api.datasource.nft.AssetScopeDAO;
import collaborate.api.transaction.Transaction;
import collaborate.api.user.metadata.UserMetadataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class GrantAccessService {

  private final AssetScopeDAO assetScopeDAO;

  private final CipherJwtService cipherService;
  private final AccessTokenProvider accessTokenProvider;
  private final ObjectMapper objectMapper;
  private final UserMetadataService userMetadataService;
  private final GrantAccessDAO grantAccessDAO;

  private final KpiService kpiService;

  public void grant(Transaction transaction) {
    AccessRequestParams accessRequestParams = getAccessRequestParams(transaction);
    kpiService.save(buildGrantKpi(transaction, accessRequestParams));
    var requester = transaction.getSource();
    // Get OAuth2 vault metadata
    VaultMetadata vaultMetadata = getVaultMetadata(accessRequestParams);

    // Get JWT
    var assetScope = assetScopeDAO.findAllById(accessRequestParams.getScopes())
        .stream()
        .map(AssetScope::getScope)
        .collect(Collectors.joining(" "));

    var accessTokenResponse = accessTokenProvider.get(
        vaultMetadata.getOAuth2(),
        Optional.of(assetScope).filter(s -> !s.isBlank())
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
        .find(datasourceId, VaultMetadata.class)
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


  Kpi buildGrantKpi(Transaction transaction, AccessRequestParams accessRequestParams) {
    return Kpi.builder()
        .createdAt(transaction.getTimestamp())
        .kpiKey("business-data.grant")
        .organizationWallet(transaction.getSource())
        .values(objectMapper.convertValue(Map.of("nft-id", accessRequestParams.getNftId()), JsonNode.class))
        .build();
  }
}
