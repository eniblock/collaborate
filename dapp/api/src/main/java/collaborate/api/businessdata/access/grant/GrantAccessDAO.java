package collaborate.api.businessdata.access.grant;

import collaborate.api.businessdata.access.grant.model.AccessGrantParams;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.job.Job;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class GrantAccessDAO {

  public static final String REQUEST_ACCESS_ENTRY_POINT = "grantAccess";
  private final ApiProperties apiProperties;
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;

  public Job grantAccess(AccessGrantParams accessGrantParams) {
    var transactions = transactionBatchFactory.createEntryPointJob(
        REQUEST_ACCESS_ENTRY_POINT,
        accessGrantParams,
        Optional.empty(),
        apiProperties.getDigitalPassportContractAddress()
    );
    return tezosApiGatewayJobClient.sendTransactionBatch(transactions, false);

  }
}
