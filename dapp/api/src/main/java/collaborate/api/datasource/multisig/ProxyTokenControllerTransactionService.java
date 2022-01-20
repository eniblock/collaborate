package collaborate.api.datasource.multisig;

import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransaction;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProxyTokenControllerTransactionService {

  private final ProxyTokenControllerTransactionDAO proxyTokenControllerTransactionDAO;

  public Optional<ProxyTokenControllerTransaction> findTransaction(String smartContract,
      Long tokenId) {
    return proxyTokenControllerTransactionDAO.findBySmartContractAndMultiSigId(smartContract, tokenId);
  }

  public List<Integer> findMultiSigIdListByOwner(String smartContract, String owner) {
    return proxyTokenControllerTransactionDAO.findAllBySmartContractAndEntrypointAndOwner(
            smartContract,
            "build",
            owner
        )
        .stream()
        .map(ProxyTokenControllerTransaction::getMultiSigId)
        .map(Long::intValue)
        .collect(Collectors.toList());
  }

  public List<Integer> findMultiSigIdListByOperator(String smartContract, String operator) {
    return proxyTokenControllerTransactionDAO.findAllBySmartContractAndEntrypointAndOperator(
            smartContract,
            "build",
            operator
        )
        .stream()
        .map(ProxyTokenControllerTransaction::getMultiSigId)
        .map(Long::intValue)
        .collect(Collectors.toList());
  }

  public void saveTransaction(String smartContract, Long tokenId, String owner, String operator,
      ZonedDateTime timestamp, String entrypoint, JsonNode parameters) {
    var transaction = new ProxyTokenControllerTransaction(
        smartContract,
        tokenId,
        owner,
        operator,
        timestamp,
        entrypoint,
        parameters);
    proxyTokenControllerTransactionDAO.save(transaction);
  }
}
