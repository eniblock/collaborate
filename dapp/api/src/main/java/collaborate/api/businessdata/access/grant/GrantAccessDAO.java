package collaborate.api.businessdata.access.grant;

import collaborate.api.businessdata.TAGBusinessDataClient;
import collaborate.api.businessdata.access.grant.model.AccessGrantParams;
import collaborate.api.businessdata.access.request.model.AccessRequest;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class GrantAccessDAO {

  public static final String GRANT_ACCESS_ENTRY_POINT = "grant_access";
  public static final String ACCESS_REQUESTS_STORAGE_FIELD = "access_requests";

  private final ApiProperties apiProperties;
  private final TAGBusinessDataClient tagBusinessDataClient;
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;

  public Job grantAccess(AccessGrantParams accessGrantParams) {
    var transactions = transactionBatchFactory.createEntryPointJob(
        GRANT_ACCESS_ENTRY_POINT,
        accessGrantParams,
        Optional.empty(),
        apiProperties.getBusinessDataContractAddress()
    );
    return tezosApiGatewayJobClient.sendTransactionBatch(transactions, false);

  }

  public Optional<AccessRequest> findOneAccessRequestById(UUID id) {
    var requestAccessRequest = new DataFieldsRequest<>(List.of(
        new MapQuery<>(ACCESS_REQUESTS_STORAGE_FIELD, List.of(id))
    ));
    var accessRequestResult = tagBusinessDataClient.getAccessRequests(
        apiProperties.getBusinessDataContractAddress(),
        requestAccessRequest
    );
    if (accessRequestResult.getAccessRequests() != null) {
      return accessRequestResult.getAccessRequests().stream()
          .filter(e -> e.getKey().equals(id))
          .filter(e -> Objects.nonNull(e.getValue()))
          .map(TagEntry::getValue)
          .findFirst();
    }
    return Optional.empty();
  }

}
