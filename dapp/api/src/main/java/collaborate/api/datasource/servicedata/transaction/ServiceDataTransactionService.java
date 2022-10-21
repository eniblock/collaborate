package collaborate.api.datasource.servicedata.transaction;

import collaborate.api.datasource.servicedata.model.transaction.ServiceDataTransaction;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceDataTransactionService {

  private final ServiceDataTransactionDAO serviceDataTransactionDAO;
  private final ObjectMapper objectMapper;

  public Optional<ZonedDateTime> findTransactionDateByTokenId(String smartContract,
      String tokenId) {
    return serviceDataTransactionDAO.findBySmartContractAndTokenId(smartContract, tokenId)
        .map(ServiceDataTransaction::getTimestamp);
  }

  public void saveServiceDataTransaction(Transaction transaction) {
    var parameters = deserializeTransactionParameters(
        transaction.getParameters());
    var smartContract = transaction.getDestination();

    var serviceDataTransaction = new ServiceDataTransaction(
        smartContract,
        parameters.getAssetId(),
        transaction.getEntrypoint(),
        parameters.getAssetId(),
        parameters.getMetadataUri(),
        parameters.getNftOperatorAddress(),
        transaction.getTimestamp(),
        transaction.getParameters()
    );
    serviceDataTransactionDAO.save(serviceDataTransaction);
  }

  private ServiceDataTransactionParameter deserializeTransactionParameters(JsonNode param) {
    try {
      return objectMapper.treeToValue(
          param,
          ServiceDataTransactionParameter.class
      );
    } catch (JsonProcessingException e) {
      log.error("While converting transactionParameters={} to ServiceDataTransactionParameters",
          param);
      throw new IllegalStateException(e);
    }
  }
}
