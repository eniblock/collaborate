package collaborate.api.datasource.multisig;

import static java.lang.String.format;

import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransaction;
import collaborate.api.datasource.multisig.model.TransactionBuildParam;
import collaborate.api.datasource.multisig.model.callparam.MintFacade;
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

  public ProxyTokenControllerTransaction getTransactionByTokenId(
      String smartContract,
      Long tokenId) {
    return proxyTokenControllerTransactionDAO.findBySmartContractAndMultiSigId(
        smartContract,
        tokenId
    ).orElseThrow(
        () -> new IllegalStateException(format(
            "No transaction found for tokenId=%s and smartContract=%s",
            tokenId,
            smartContract
        ))
    );
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
    var buildParam = convertToTransactionBuildParam(transaction.getParameters());
    var mintFacade = new MintFacade(buildParam);
    var smartContract = transaction.getDestination();
    var isSigned = false;

    var proxyTokenControllerTransaction = new ProxyTokenControllerTransaction(
        smartContract,
        buildParam.getMultisigId(),
        mintFacade.getOwner(),
        mintFacade.getOperator(),
        transaction.getTimestamp(),
        mintFacade.getIpfsMetadataURI(),
        transaction.getEntrypoint(),
        isSigned,
        transaction.getParameters());
    proxyTokenControllerTransactionDAO.save(proxyTokenControllerTransaction);
  }

  public void flagAsSigned(Transaction transaction) {
    var smartContract = transaction.getDestination();
    // for sign transaction multisigId is the only parameter
    var tokenId = Long.valueOf(transaction.getParameters().asText());
    var buildTransaction = getTransactionByTokenId(smartContract, tokenId);
    buildTransaction.setIsSigned(true);
    proxyTokenControllerTransactionDAO.save(buildTransaction);
  }

  private TransactionBuildParam convertToTransactionBuildParam(JsonNode param) {
    try {
      return objectMapper.treeToValue(
          param,
          TransactionBuildParam.class
      );
    } catch (JsonProcessingException e) {
      log.error("While converting transactionParameters={} to MultisigBuildParam", param);
      throw new IllegalStateException(e);
    }
  }
}
