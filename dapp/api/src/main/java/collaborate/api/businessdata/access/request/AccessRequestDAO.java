package collaborate.api.businessdata.access.request;

import static collaborate.api.tag.TezosApiGatewayJobClient.ORGANIZATION_SECURE_KEY_NAME;
import static java.util.stream.Collectors.toList;

import collaborate.api.businessdata.access.request.model.AccessRequestParams;
import collaborate.api.config.UUIDGenerator;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.passport.model.DatasourceDTO;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.TransactionBatchFactory;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.job.Transaction;
import collaborate.api.tag.model.job.TransactionBatch;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AccessRequestDAO {

  public static final String REQUEST_ACCESS_ENTRY_POINT = "requestAccess";
  private final ApiProperties apiProperties;
  private final TezosApiGatewayJobClient tezosApiGatewayClient;
  private final TransactionBatchFactory transactionBatchFactory;
  private final UUIDGenerator uuidGenerator;

  public Job accessRequest(List<AssetDetailsDTO> assetDetailsDTOs, String requesterWallet) {
    var transactions = assetDetailsDTOs.stream()
        .flatMap(a -> toTransactionsStream(a, requesterWallet))
        .collect(toList());

    return tezosApiGatewayClient.sendTransactionBatch(
        new TransactionBatch<>(transactions, ORGANIZATION_SECURE_KEY_NAME));
  }

  Stream<Transaction<AccessRequestParams>> toTransactionsStream(AssetDetailsDTO assetDetailsDTO,
      String requesterWallet) {
    return assetDetailsDTO.getAssetDataCatalog().getDatasources().stream()
        .map(d -> toAccessRequestParam(
                d,
                requesterWallet,
                // FIXME provider Address should be valued from the datasource owner field
                assetDetailsDTO.getAssetOwner().getAddress()
            )
        )
        .map(this::toTransaction);
  }

  public Transaction<AccessRequestParams> toTransaction(AccessRequestParams accessRequest) {
    return Transaction.<AccessRequestParams>builder()
        .contractAddress(apiProperties.getBusinessDataContractAddress())
        .entryPoint(REQUEST_ACCESS_ENTRY_POINT)
        .entryPointParams(accessRequest)
        .build();
  }

  AccessRequestParams toAccessRequestParam(
      DatasourceDTO datasourceDTO,
      String requesterWallet,
      String providerAddress
  ) {
    return AccessRequestParams.builder()
        .scope(datasourceDTO.getAssetIdForDatasource())
        .datasourceId(datasourceDTO.getId())
        .scope(datasourceDTO.getAssetIdForDatasource())
        .requesterAddress(requesterWallet)
        .providerAddress(providerAddress)
        .build();
  }
}
