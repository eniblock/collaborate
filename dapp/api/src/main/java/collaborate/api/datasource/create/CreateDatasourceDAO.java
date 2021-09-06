package collaborate.api.datasource.create;

import static java.util.Optional.empty;

import collaborate.api.datasource.domain.Datasource;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.job.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CreateDatasourceDAO {

  private static final String CREATE_DATASOURCE_ENTRY_POINT = "createDatasource";
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;

  public Job create(Datasource datasource) {
    // TODO Implements IPFS
    String cid = RandomStringUtils.randomAlphabetic(10);
    var transactions = transactionBatchFactory.createEntryPointJob(
        CREATE_DATASOURCE_ENTRY_POINT,
        new CreateDatasourceEntryPointParam(datasource.getId(), cid),
        empty()
    );
    return tezosApiGatewayJobClient.sendTransactionBatch(transactions);
  }
}
