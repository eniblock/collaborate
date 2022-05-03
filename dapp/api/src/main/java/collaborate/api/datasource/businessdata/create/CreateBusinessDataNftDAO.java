package collaborate.api.datasource.businessdata.create;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.create.DataCatalogCreationDTO;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.job.Transaction;
import collaborate.api.user.UserService;
import feign.FeignException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
  private final String businessDataContractAddress;
  private final UserService userService;

  public static final String DATA_CATALOG_CREATION_ENTRYPOINT = "create_business_datasource";

  public Job mintBusinessDataNFT(List<AssetIdAndUri> assetIdAndUris) {
    var transactions = assetIdAndUris.stream()
        .map(a -> DataCatalogCreationDTO.builder()
            .nftOperatorAddress(userService.getAdminUser().getAddress())
            .assetId(a.getAssetId())
            .metadataUri(new Bytes(a.getUri()))
            .build()
        ).map(p -> Transaction.builder()
            .entryPoint(DATA_CATALOG_CREATION_ENTRYPOINT)
            .contractAddress(businessDataContractAddress)
            .entryPointParams(p)
            .build()
        ).collect(Collectors.toList());

    var transactionBatch = transactionBatchFactory.createEntryPointBatchJob(
        transactions,
        Optional.empty()
    );

    try {
      return tezosApiGatewayJobClient.sendTransactionBatch(transactionBatch, false);
    } catch (FeignException e) {
      log.error("While minting business-data nft with TAG", e);
      throw new ResponseStatusException(
          HttpStatus.BAD_GATEWAY,
          "Can't send request to Tezos-API-Gateway"
      );
    }
  }
}
