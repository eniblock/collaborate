package collaborate.api.businessdata.access.grant;

import collaborate.api.businessdata.TAGBusinessDataClient;
import collaborate.api.businessdata.access.grant.model.AccessGrantParams;
import collaborate.api.businessdata.access.request.model.AccessRequest;
import collaborate.api.businessdata.access.request.model.AccessRequestParams;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class GrantAccessDAO {

  public static final String GRANT_ACCESS_ENTRY_POINT = "grantAccess";
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
        apiProperties.getDigitalPassportContractAddress()
    );
    return tezosApiGatewayJobClient.sendTransactionBatch(transactions, false);

  }

  public Optional<TagEntry<UUID, AccessRequest>> findAccessRequest(
      AccessRequestParams accessRequestParams, String requester, String provider) {
    var requestAccessRequest = new DataFieldsRequest<>(List.of(
        new MapQuery<>(ACCESS_REQUESTS_STORAGE_FIELD, null)
    ));
    Predicate<AccessRequest> requestPredicate = (AccessRequest a) ->
        a.getScopes().equals(accessRequestParams.getScopes())
            && a.getRequesterAddress().equals(requester)
            && a.getProviderAddress().equals(provider)
            && a.getTokenId().equals(accessRequestParams.getNftId());

    return tagBusinessDataClient
        .getAccessRequests(
            apiProperties.getDigitalPassportContractAddress(),
            requestAccessRequest
        ).getAccessRequests().stream()
        .filter(e -> requestPredicate.test(e.getValue()))
        .findFirst();
  }
}
