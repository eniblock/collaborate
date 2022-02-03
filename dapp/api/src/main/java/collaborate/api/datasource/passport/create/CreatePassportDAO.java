package collaborate.api.datasource.passport.create;

import static collaborate.api.datasource.nft.model.storage.TokenMetadata.TOKEN_METADATA_FIELD;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.proxytokencontroller.MultisigBuildCallParamMint;
import collaborate.api.tag.model.proxytokencontroller.MultisigBuildParam;
import collaborate.api.tag.model.proxytokencontroller.MultisigMetadata;
import collaborate.api.tag.model.proxytokencontroller.dto.MultisigBuildCallParamMintDetails;
import collaborate.api.tag.model.proxytokencontroller.dto.MultisigBuildCallParamMintDetailsMint;
import collaborate.api.tag.model.proxytokencontroller.dto.MultisigBuildCallParamMintDetailsMintParams;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.user.UserService;
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
  private final UserService userService;

  private static final String CREATION_ENTRY_POINT = "build";

  public Job create(String metadataUri, String vehicleOwnerAddress) {
    var multisigId = tezosApiGatewayPassportCreationClient.getMultisigNb(
        apiProperties.getDigitalPassportProxyTokenControllerContractAddress(),
        new DataFieldsRequest<>(List.of("multisig_nb"))
    );

    var transactions = transactionBatchFactory.createEntryPointJob(
        CREATION_ENTRY_POINT,
        buildCreateEntryPointParam(metadataUri, vehicleOwnerAddress, multisigId.getMultisigNb()),
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

  private MultisigBuildParam buildCreateEntryPointParam(
      String metadataUri, String vehicleOwnerAddress, long multisigId) {

    return MultisigBuildParam.builder()
        .buildAndSign(true)
        .multisigId(multisigId)
        .signers(List.of(vehicleOwnerAddress))
        .callParams(
            MultisigBuildCallParamMint.builder()
                .targetAddress(apiProperties.getDigitalPassportContractAddress())
                .parameters(
                    MultisigBuildCallParamMintDetails.builder()
                        .mint(
                            MultisigBuildCallParamMintDetailsMint.builder()
                                .operator(userService.getAdminUser().getAddress())
                                .mintParams(
                                    MultisigBuildCallParamMintDetailsMintParams.builder()
                                        .amount(1)
                                        .address(vehicleOwnerAddress)
                                        .metadata(List.of(
                                            MultisigMetadata.builder()
                                                .key(TOKEN_METADATA_FIELD)
                                                .value(new Bytes(metadataUri))
                                                .build()
                                        )).build()
                                ).build()
                        ).build()
                ).build()
        ).build();
  }

}
