package collaborate.api.datasource.businessdata.create;

import collaborate.api.datasource.create.DataCatalogCreationDTO;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.job.Transaction;
import collaborate.api.user.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePassportDataNftDAO {

  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;
  private final String businessDataContractAddress; // TODO: use passport contract ?
  private final UserService userService;

  public static final String CREATE_DATASOURCE_ENTRYPOINT = "create_business_datasource";

  public Job mintPassportDataNFT(List<AssetIdAndUri> assetIdAndUris) {
    var transactions = assetIdAndUris.stream()
        .map(asset ->
            DataCatalogCreationDTO.builder()
                .nftOperatorAddress(userService.getAdminUser().getAddress())
                .assetId(asset.getAssetId())
                .metadataUri(new Bytes(asset.getUri()))
                .build()
        ).map(
            catalogCreationDTO -> Transaction.builder()
                .entryPoint(CREATE_DATASOURCE_ENTRYPOINT)
                .contractAddress(businessDataContractAddress)
                .entryPointParams(catalogCreationDTO)
                .build()
        ).collect(Collectors.toList());

    var transactionBatch = transactionBatchFactory.createEntryPointBatchJob(
        transactions,
        Optional.empty()
    );

    return tezosApiGatewayJobClient.sendTransactionBatch(transactionBatch, false);
  }
}
