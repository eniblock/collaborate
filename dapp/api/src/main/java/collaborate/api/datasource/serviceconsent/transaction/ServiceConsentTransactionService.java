package collaborate.api.datasource.serviceconsent.transaction;

import collaborate.api.datasource.kpi.Kpi;
import collaborate.api.datasource.kpi.KpiService;
import collaborate.api.datasource.serviceconsent.model.transaction.Fa2ServiceConsentTransaction;
import collaborate.api.datasource.serviceconsent.model.transaction.MintTransactionParameters;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceConsentTransactionService {

  private final ServiceConsentTransactionDAO serviceConsentTransactionDAO;
  private final String serviceConsentContractAddress;
  private final KpiService kpiService;

  private final ObjectMapper objectMapper;

  public Optional<ZonedDateTime> findTransactionDateByTokenId(String smartContract, Long tokenId) {
    return serviceConsentTransactionDAO.findBySmartContractAndTokenId(smartContract, tokenId)
        .map(Fa2ServiceConsentTransaction::getTimestamp);
  }

  public void saveFa2Transaction(Transaction transaction) {
    var parameters = parseMintParams(
        transaction.getParameters());
    var smartContract = transaction.getDestination();
    var timestamp = transaction.getTimestamp();

    var fa2Transaction = new Fa2ServiceConsentTransaction(
        smartContract,
        parameters.getTokenId(),
        transaction.getEntrypoint(),
        parameters.getOwner(),
        parameters.getIpfsMetadataURI(),
        parameters.getPassportId(),
        timestamp,
        transaction.getParameters()
    );
    serviceConsentTransactionDAO.save(fa2Transaction);
  }

  private MintTransactionParameters parseMintParams(JsonNode param) {
    try {
      return objectMapper.treeToValue(
          param,
          MintTransactionParameters.class
      );
    } catch (JsonProcessingException e) {
      log.error("While converting transactionParameters={} to Fa2TransactionParameters", param);
      throw new IllegalStateException(e);
    }
  }

  public void saveNftCreated(Transaction transaction) {
    var mintParams = parseMintParams(transaction.getParameters());
    Kpi.builder()
        .createdAt(transaction.getTimestamp())
        .kpiKey("nft.created")
        .organizationWallet(transaction.getSource())
        .values(objectMapper.convertValue(Map.of(
            "contract", serviceConsentContractAddress,
            "tokenId", mintParams.getTokenId(),
            "owner", mintParams.getOwner()
        ), JsonNode.class))
        .build();

  }
}
