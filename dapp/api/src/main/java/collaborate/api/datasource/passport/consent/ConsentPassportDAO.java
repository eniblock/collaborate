package collaborate.api.datasource.passport.consent;

import collaborate.api.config.api.ApiProperties;
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
class ConsentPassportDAO {

  private static final String CONSENT_ENTRY_POINT = "sign";
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;
  private final String businessDataContractAddress;

  public Job consent(ConsentPassportDTO consentPassportDTO) {
    var transactions = transactionBatchFactory.createEntryPointJob(
        CONSENT_ENTRY_POINT,
        consentPassportDTO.getContractId(),
        Optional.of(consentPassportDTO.getVehicleOwnerUserWallet().getUserId()),
        businessDataContractAddress
    );
    return tezosApiGatewayJobClient.sendTransactionBatch(transactions, false);
  }

}
