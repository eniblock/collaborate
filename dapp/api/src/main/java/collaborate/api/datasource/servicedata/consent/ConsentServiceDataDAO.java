package collaborate.api.datasource.servicedata.consent;

import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.job.Job;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class ConsentServiceDataDAO {

  private static final String CONSENT_ENTRY_POINT = "sign";
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;
  private final String serviceDataProxyControllerContractAddress;

  public Job consent(ConsentServiceDataDTO consentServiceDataDTO) {
    var transactions = transactionBatchFactory.createEntryPointJob(
        CONSENT_ENTRY_POINT,
        consentServiceDataDTO.getContractId(),
        Optional.of(consentServiceDataDTO.getVehicleOwnerUserWallet().getUserId()),
        serviceDataProxyControllerContractAddress
    );
    return tezosApiGatewayJobClient.sendTransactionBatch(transactions, false);
  }

}
