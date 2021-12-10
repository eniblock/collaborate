package collaborate.api.datasource.passport.create;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.proxytokencontroller.MultisigBuildCallParamMint;
import collaborate.api.tag.model.proxytokencontroller.MultisigBuildParam;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import feign.FeignException;
import java.util.List;
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
  private final TezosApiGatewayPassportCreationClient tezosApiGatewayPassportCreationClient;

  private static final String CREATION_ENTRY_POINT = "build";

  public Job create(String metadataUri, String vehicleOwnerAddress) {
    var multisigId = tezosApiGatewayPassportCreationClient.getMultisigNb(
        apiProperties.getDigitalPassportProxyTokenControllerContractAddress(),
        new DataFieldsRequest<>(List.of("multisig_nb"))
    );

    if (2 == 1 + 1) {
      return null;
    }

    var transactions = transactionBatchFactory.createEntryPointJob(
        CREATION_ENTRY_POINT,
        buildCreateEntryPointParam(metadataUri, vehicleOwnerAddress),
        Optional.empty(),
        apiProperties.getDigitalPassportProxyTokenControllerContractAddress()
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

  private MultisigBuildParam<MultisigBuildCallParamMint> buildCreateEntryPointParam(
      String metadataUri, String vehicleOwnerAddress) {

    return null;

    /*MultisigBuildParam.builder()
        .buildAndSign(true)
        .multisigId(-1)


     */
  }

}
