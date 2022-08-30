package collaborate.api.datasource.businessdata.create;

import collaborate.api.datasource.create.MintBusinessDataParams;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
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
public class CreateBusinessDataNftDAO {

  private final TezosApiGatewayJobClient tezosApiGatewayJobClient;
  private final TransactionBatchFactory transactionBatchFactory;
  private final String businessDataContractAddress;
  private final UserService userService;

  public static final String CREATE_DATASOURCE_ENTRYPOINT = "create_business_datasource";

  public Job mintBusinessDataNFT(List<AssetMetadataMintDTO> assetIdAndUrises) {
    var transactions = assetIdAndUrises.stream()
        .map(asset ->
            MintBusinessDataParams.builder()
                .nftOperatorAddress(userService.getAdminUser().getAddress())
                .assetId(asset.getAssetId())
                .metadata(asset.getMetadata())
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
