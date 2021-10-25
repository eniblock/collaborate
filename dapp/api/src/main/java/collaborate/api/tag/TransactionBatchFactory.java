package collaborate.api.tag;

import static collaborate.api.tag.TezosApiGatewayJobClient.ORGANIZATION_SECURE_KEY_NAME;

import collaborate.api.tag.model.job.Transaction;
import collaborate.api.tag.model.job.TransactionBatch;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionBatchFactory {

  public <T> TransactionBatch<T> createEntryPointJob(String entryPoint, T entryPointParam,
      Optional<String> sender, String contractAddress) {
    return new TransactionBatch<>(
        List.of(Transaction.<T>builder()
            .entryPoint(entryPoint)
            .contractAddress(contractAddress)
            .entryPointParams(entryPointParam)
            .build()),
        sender.orElse(ORGANIZATION_SECURE_KEY_NAME)
    );
  }

  public TransactionBatch createEntryPointBatchJob(
      // No generic, because this is a transaction list with heterogeneous params
      List<Transaction<Object>> transactions,
      Optional<String> sender) {
    return new TransactionBatch(
        transactions,
        sender.orElse(ORGANIZATION_SECURE_KEY_NAME)
    );
  }

}
