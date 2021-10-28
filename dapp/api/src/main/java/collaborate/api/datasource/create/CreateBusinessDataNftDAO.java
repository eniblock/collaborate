package collaborate.api.datasource.create;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.user.UserService;
import feign.FeignException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateBusinessDataNftDAO {

  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;
  private final ApiProperties apiProperties;
  private final TezosApiGatewayBusinessDataDatasourceClient tezosApiGatewayBusinessDataDatasourceClient;
  private final UserService userService;

  private static final String DATA_CATALOG_CREATION_ENTRYPOINT = "create_business_datasource";

  public Job mintBusinessDataNFT(UUID assetId, String ipfsMetadataUri) {
    var paramsDataCatalogCreation = DataCatalogCreationDTO.builder()
        .nftOperatorAddress(userService.getAdminUser().getAddress())
        .assetId(assetId.toString())
        .metadataUri(new Bytes(ipfsMetadataUri))
        .build();

    var transactionBatch = transactionBatchFactory.createEntryPointJob(
        DATA_CATALOG_CREATION_ENTRYPOINT,
        paramsDataCatalogCreation,
        Optional.empty(),
        apiProperties.getBusinessDataContractAddress()
    );

    Job job;
    try {
      job = tezosApiGatewayJobClient.sendTransactionBatch(transactionBatch, false);
    } catch (FeignException e) {
      log.error("Problem with TAG", e);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY,
          "Can't send request to Tezos-API-Gateway"
      );
    }
    return job;
  }

  public long getMultisigCounter() {
    var requestTokenCount = new DataFieldsRequest<>(List.of("multisig_counter"));
    return tezosApiGatewayBusinessDataDatasourceClient
        .getMultisigCounter(
            apiProperties.getBusinessDataContractAddress(),
            requestTokenCount
        ).getMultisigCounter();
  }

}
