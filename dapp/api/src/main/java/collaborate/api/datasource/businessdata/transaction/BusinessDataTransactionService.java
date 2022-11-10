package collaborate.api.datasource.businessdata.transaction;

import collaborate.api.datasource.businessdata.model.transaction.BusinessDataTransaction;
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
public class BusinessDataTransactionService {

  private final BusinessDataTransactionDAO businessDataTransactionDAO;
  private final ObjectMapper objectMapper;

  public Optional<ZonedDateTime> findTransactionDateByTokenId(String smartContract,
      String tokenId) {
    return businessDataTransactionDAO.findBySmartContractAndTokenId(smartContract, tokenId)
        .map(BusinessDataTransaction::getTimestamp);
  }

  public void saveBusinessDataTransaction(Transaction transaction) {
    var parameters = deserializeTransactionParameters(
        transaction.getParameters());
    var smartContract = transaction.getDestination();

    var businessDataTransaction = new BusinessDataTransaction(
        smartContract,
        parameters.getAssetId(),
        transaction.getEntrypoint(),
        parameters.getAssetId(),
        parameters.getMetadata(),
        parameters.getNftOperatorAddress(),
        transaction.getTimestamp(),
        transaction.getParameters()
    );
    businessDataTransactionDAO.save(businessDataTransaction);
  }

  private BusinessDataTransactionParameter deserializeTransactionParameters(JsonNode param) {
    try {
      return objectMapper.treeToValue(
          param,
          BusinessDataTransactionParameter.class
      );
    } catch (JsonProcessingException e) {
      log.error("While converting transactionParameters={} to BusinessDataTransactionParameters",
          param);
      throw new IllegalStateException(e);
    }
  }
}
