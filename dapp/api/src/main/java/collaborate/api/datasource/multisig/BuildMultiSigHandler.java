package collaborate.api.datasource.multisig;

import collaborate.api.transaction.Transaction;
import collaborate.api.transaction.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildMultiSigHandler implements TransactionHandler {

  private final ProxyTokenControllerTransactionService proxyTokenControllerTransactionService;

  @Override
  public void handle(Transaction transaction) {
    if (transaction.getEntrypoint().equals("build")) {
      var smartContract = transaction.getDestination();
      var tokenId = Long.valueOf(transaction.getParameters().get("multisig_id").asText());
      var timestamp = transaction.getTimestamp();
      //FIXME check stucture with tag
      var parameters = transaction.getParameters()
          .get("call_params")
          .get("parameters");
      var mint = transaction.getIndexer().equals("tzstats")
          ? parameters.get("@or_0").get("mint")
          : parameters.get("mint");
      var owner = mint
          .get("mint_params")
          .get("address")
          .asText();
      var operator = mint
          .get("operator")
          .asText();

      proxyTokenControllerTransactionService.saveTransaction(
          smartContract,
          tokenId,
          owner,
          operator,
          timestamp,
          transaction.getEntrypoint(),
          transaction.getParameters()
      );
    }

    if (transaction.getEntrypoint().equals("sign")) {
      var smartContract = transaction.getDestination();
      var tokenId = Long.valueOf(transaction.getParameters().asText());
      var timestamp = transaction.getTimestamp();
      var buildTransaction = proxyTokenControllerTransactionService
          .findTransaction(smartContract, tokenId).orElseThrow();

      proxyTokenControllerTransactionService.saveTransaction(
          smartContract,
          tokenId,
          buildTransaction.getOwner(),
          buildTransaction.getOperator(),
          timestamp,
          transaction.getEntrypoint(),
          buildTransaction.getParameters()
      );
    }

  }
}
