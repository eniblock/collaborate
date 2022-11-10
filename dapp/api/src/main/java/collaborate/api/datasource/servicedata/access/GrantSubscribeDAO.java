package collaborate.api.datasource.servicedata.access;

import collaborate.api.datasource.servicedata.TAGServiceDataClient;
import collaborate.api.datasource.servicedata.access.model.AccessGrantParams;
import collaborate.api.datasource.servicedata.access.model.AccessRequest;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
class GrantSubscribeDAO {

  public static final String GRANT_ACCESS_ENTRY_POINT = "grant_access";
  public static final String ACCESS_REQUESTS_STORAGE_FIELD = "access_requests";

  private final String businessDataContractAddress;
  private final TAGServiceDataClient tagBusinessDataClient;
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;

  public Job grantAccess(String cipheredToken, String requester, Integer nftId) {
    var accessGrantParams = toAccessGrantParams(cipheredToken, requester, nftId);
    var transactions = transactionBatchFactory.createEntryPointJob(
        GRANT_ACCESS_ENTRY_POINT,
        accessGrantParams,
        Optional.empty(),
        businessDataContractAddress
    );
    return tezosApiGatewayJobClient.sendTransactionBatch(transactions, false);
  }

  AccessGrantParams toAccessGrantParams(String cipheredToken, String requester, Integer nftId) {
    try {
      return AccessGrantParams.builder()
          .requesterAddress(requester)
          .cipheredToken(cipheredToken)
          .nftId(nftId)
          .build();
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new IllegalStateException(e);
    }
  }

  public Optional<AccessRequest> findOneAccessRequestById(UUID id) {
    var requestAccessRequest = new DataFieldsRequest<>(List.of(
        new MapQuery<>(ACCESS_REQUESTS_STORAGE_FIELD, List.of(id))
    ));
    var accessRequestResult = tagBusinessDataClient.getAccessRequests(
        businessDataContractAddress,
        requestAccessRequest
    );
    return Optional.ofNullable(accessRequestResult.getAccessRequests())
        .flatMap(accessRequest -> TagEntry.findFirstNonNullValueByKey(accessRequest, id));

  }

}
