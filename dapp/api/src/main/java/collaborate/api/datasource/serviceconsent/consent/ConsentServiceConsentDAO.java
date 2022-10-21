package collaborate.api.datasource.serviceconsent.consent;

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
class ConsentServiceConsentDAO {

  private static final String CONSENT_ENTRY_POINT = "sign";
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;
  private final String serviceConsentProxyControllerContractAddress;

  public Job consent(ConsentServiceConsentDTO consentServiceConsentDTO) {
    var transactions = transactionBatchFactory.createEntryPointJob(
        CONSENT_ENTRY_POINT,
        consentServiceConsentDTO.getContractId(),
        Optional.of(consentServiceConsentDTO.getVehicleOwnerUserWallet().getUserId()),
        serviceConsentProxyControllerContractAddress
    );
    return tezosApiGatewayJobClient.sendTransactionBatch(transactions, false);
  }

}
