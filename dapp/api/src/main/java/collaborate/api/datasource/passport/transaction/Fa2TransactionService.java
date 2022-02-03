package collaborate.api.datasource.passport.transaction;

import collaborate.api.datasource.passport.model.transaction.Fa2Transaction;
import collaborate.api.datasource.passport.model.transaction.Fa2TransactionParameters;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Fa2TransactionService {

  private final Fa2TransactionDAO fa2TransactionDAO;
  private final ObjectMapper objectMapper;

  public ZonedDateTime getTransactionDateByTokenId(String smartContract, Long tokenId) {
    return fa2TransactionDAO.findBySmartContractAndTokenId(smartContract, tokenId)
        .map(Fa2Transaction::getTimestamp).orElse(null);
  }

  public void saveFa2Transaction(Transaction transaction) {
    var parameters = deserializeTransactionParameters(
        transaction.getParameters());
    var smartContract = transaction.getDestination();
    var timestamp = transaction.getTimestamp();

    var fa2Transaction = new Fa2Transaction(
        smartContract,
        parameters.getTokenId(),
        transaction.getEntrypoint(),
        parameters.getAddress(),
        parameters.getIpfsMetadataURI(),
        timestamp,
        transaction.getParameters()
        );
    fa2TransactionDAO.save(fa2Transaction);
  }

  private Fa2TransactionParameters deserializeTransactionParameters(JsonNode param) {
    try {
      return objectMapper.treeToValue(
          param,
          Fa2TransactionParameters.class
      );
    } catch (JsonProcessingException e) {
      log.error("While converting transactionParameters={} to Fa2TransactionParameters", param);
      throw new IllegalStateException(e);
    }
  }
}
