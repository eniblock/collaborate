package collaborate.api.passport.consent;

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
public class ConsentPassportDAO {

  private static final String CONSENT_ENTRY_POINT = "passportConsent";
  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;

  public Job consent(ConsentPassportDTO consentPassportDTO) {
    var transactions = transactionBatchFactory.createEntryPointJob(
        CONSENT_ENTRY_POINT,
        buildConsentEntryPointParam(consentPassportDTO),
        Optional.of(consentPassportDTO.getVehicleOwnerUserWallet().getUserId())
    );
    return tezosApiGatewayJobClient.sendTransactionBatch(transactions, false);
  }

  private PassportConsentEntryPointParam buildConsentEntryPointParam(
      ConsentPassportDTO consentPassportDTO) {
    String vehicleOwnerAddress = consentPassportDTO.getVehicleOwnerUserWallet().getAddress();
    return PassportConsentEntryPointParam.builder()
        .vehicleOwnerAddress(vehicleOwnerAddress)
        .contractId(consentPassportDTO.getContractId())
        .build();
  }
}
