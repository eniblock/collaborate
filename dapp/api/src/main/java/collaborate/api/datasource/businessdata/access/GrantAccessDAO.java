package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.businessdata.TAGBusinessDataClient;
import collaborate.api.datasource.businessdata.access.model.AccessGrantParams;
import collaborate.api.datasource.businessdata.access.model.AccessRequest;
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
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class GrantAccessDAO {

  public static final String GRANT_ACCESS_ENTRY_POINT = "grant_access";
  public static final String ACCESS_REQUESTS_STORAGE_FIELD = "access_requests";

  private final String businessDataContractAddress;
  private final TAGBusinessDataClient tagBusinessDataClient;
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;

  public Job grantAccess(AccessGrantParams accessGrantParams) {
    var transactions = transactionBatchFactory.createEntryPointJob(
        GRANT_ACCESS_ENTRY_POINT,
        accessGrantParams,
        Optional.empty(),
        businessDataContractAddress
    );
    return tezosApiGatewayJobClient.sendTransactionBatch(transactions, false);

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
