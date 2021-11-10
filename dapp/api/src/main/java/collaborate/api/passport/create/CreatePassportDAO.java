package collaborate.api.passport.create;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.job.Job;
import feign.FeignException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
class CreatePassportDAO {

  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;
  private final ApiProperties apiProperties;

  private static final String CREATION_ENTRY_POINT = "init_passport_creation";

  public Job create(String metadataUri, String vehicleOwnerAddress, String assetId) {
    var transactions = transactionBatchFactory.createEntryPointJob(
        CREATION_ENTRY_POINT,
        buildCreateEntryPointParam(metadataUri, vehicleOwnerAddress, assetId),
        Optional.empty(),
        apiProperties.getDigitalPassportContractAddress()
    );

    Job job;
    try {
      job = tezosApiGatewayJobClient.sendTransactionBatch(transactions, false);
    } catch (FeignException e) {
      log.error("Problem with TAG", e);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY,
          "Can't send request to Tezos-API-Gateway"
      );
    }

    return job;
  }

  private InitPassportCreationEntryPointParam buildCreateEntryPointParam(
      String metadataUri, String vehicleOwnerAddress, String assetId) {

    return InitPassportCreationEntryPointParam.builder()
        .metadataUri(new Bytes(metadataUri))
        .nftOwnerAddress(vehicleOwnerAddress)
        .assetId(assetId)
        .build();
  }

}
