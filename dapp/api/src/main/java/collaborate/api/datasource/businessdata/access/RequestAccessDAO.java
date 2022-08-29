package collaborate.api.datasource.businessdata.access;

import static collaborate.api.tag.TezosApiGatewayJobClient.ORGANIZATION_SECURE_KEY_NAME;
import static java.util.stream.Collectors.toList;

import collaborate.api.datasource.businessdata.access.model.AccessRequestParams;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.job.Transaction;
import collaborate.api.tag.model.job.TransactionBatch;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class RequestAccessDAO {

  public static final String REQUEST_ACCESS_ENTRY_POINT = "request_access";
  private final String businessDataContractAddress;
  private final TezosApiGatewayJobClient tezosApiGatewayClient;

  public Job accessRequest(List<AccessRequestParams> accessRequestParams) {
    var transactions = accessRequestParams.stream()
        .map(this::toTransaction)
        .collect(toList());

    return tezosApiGatewayClient.sendTransactionBatch(
        new TransactionBatch<>(transactions, ORGANIZATION_SECURE_KEY_NAME),
        false
    );
  }

  Transaction<AccessRequestParams> toTransaction(AccessRequestParams accessRequestParams) {
    return Transaction.<AccessRequestParams>builder()
        .contractAddress(businessDataContractAddress)
        .entryPoint(REQUEST_ACCESS_ENTRY_POINT)
        .entryPointParams(accessRequestParams)
        .build();
  }

}
