package collaborate.api.datasource.multisig;

import static java.lang.String.format;

import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransaction;
import collaborate.api.datasource.multisig.model.TransactionBuildParam;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProxyTokenControllerTransactionService {

  private final ProxyTokenControllerTransactionDAO proxyTokenControllerTransactionDAO;
  private final ObjectMapper objectMapper;

  public static final String BUILD_ENTRYPOINT = "build";

  public ProxyTokenControllerTransaction findTransactionByTokenId(String smartContract,
      Long tokenId) {
    return proxyTokenControllerTransactionDAO.findBySmartContractAndMultiSigId(smartContract,
        tokenId).orElseThrow(
        () -> new IllegalStateException(format("Transaction with id=%s not found", tokenId)));
  }

  public List<ProxyTokenControllerTransaction> findMultiSigListTransactionByOwner(
      String smartContract, String owner) {
    return proxyTokenControllerTransactionDAO
        .findAllBySmartContractAndEntrypointAndIsSignedAndOwner(
            smartContract,
            BUILD_ENTRYPOINT,
            false,
            owner
        );
  }

  public List<ProxyTokenControllerTransaction> findMultiSigListTransactionByOperator(
      String smartContract, String operator) {
    return proxyTokenControllerTransactionDAO
        .findAllBySmartContractAndEntrypointAndIsSignedAndOperator(
            smartContract,
            BUILD_ENTRYPOINT,
            false,
            operator
        );
  }

  public void saveTransaction(Transaction transaction) {
    var parameters = this.getParametersAsPojo(transaction.getParameters());

    var smartContract = transaction.getDestination();
    var tokenId = parameters.getMultisigId();
    var owner = parameters.getCallParams()
        .getParameters().getMint().getMintParams().getAddress();
    var operator = parameters.getCallParams()
        .getParameters().getMint().getOperator();
    var timestamp = transaction.getTimestamp();
    var metadata = parameters.getCallParams()
        .getParameters().getMint().getMintParams().getIpfsMetadata().toString();
    var entrypoint = transaction.getEntrypoint();
    var isSigned = false;

    var proxyTokenControllerTransaction = new ProxyTokenControllerTransaction(
        smartContract,
        tokenId,
        owner,
        operator,
        timestamp,
        metadata,
        entrypoint,
        isSigned,
        transaction.getParameters());
    proxyTokenControllerTransactionDAO.save(proxyTokenControllerTransaction);
  }

  public void updateTransaction(Transaction transaction) {
    var smartContract = transaction.getDestination();
    // for sign transaction multisigId is the only parameter
    var tokenId = Long.valueOf(transaction.getParameters().asText());
    var timestamp = transaction.getTimestamp();
    var buildTransaction = findTransactionByTokenId(
        smartContract, tokenId);
    var isSigned = true;

    var proxyTokenControllerTransaction = new ProxyTokenControllerTransaction(
        smartContract,
        tokenId,
        buildTransaction.getOwner(),
        buildTransaction.getOperator(),
        timestamp,
        buildTransaction.getMetadata(),
        buildTransaction.getEntrypoint(),
        isSigned,
        buildTransaction.getParameters());
    proxyTokenControllerTransactionDAO.save(proxyTokenControllerTransaction);
  }

  private TransactionBuildParam getParametersAsPojo(
      JsonNode param) {
    try {
      return objectMapper.treeToValue(
          param,
          TransactionBuildParam.class
      );
    } catch (JsonProcessingException e) {
      log.error(
          "While converting transactionParameters={} to MultisigBuildParam", param
      );
      throw new IllegalStateException(e);
    }
  }
}
