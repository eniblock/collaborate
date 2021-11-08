package collaborate.api.businessdata.access.request;

import static collaborate.api.tag.TezosApiGatewayJobClient.ORGANIZATION_SECURE_KEY_NAME;
import static java.util.stream.Collectors.toList;

import collaborate.api.businessdata.access.request.model.AccessRequestParams;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.passport.model.DatasourceDTO;
import collaborate.api.tag.TezosApiGatewayJobClient;
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

  public static final String REQUEST_ACCESS_ENTRY_POINT = "request_access";
  private final ApiProperties apiProperties;
  private final TezosApiGatewayJobClient tezosApiGatewayClient;

  public Job accessRequest(List<AssetDetailsDTO> assetDetailsDTOs) {
    var transactions = assetDetailsDTOs.stream()
        .flatMap(this::toTransactionsStream)
        .collect(toList());

    return tezosApiGatewayClient.sendTransactionBatch(
        new TransactionBatch<>(transactions, ORGANIZATION_SECURE_KEY_NAME),
        false
    );
  }

  Stream<Transaction<AccessRequestParams>> toTransactionsStream(AssetDetailsDTO assetDetailsDTO) {
    return assetDetailsDTO.getAssetDataCatalog().getDatasources().stream()
        .map(d -> toAccessRequestParam(
                assetDetailsDTO.getTokenId(),
                d,
                // FIXME provider Address should be valued from the datasource owner field
                assetDetailsDTO.getAssetOwner().getAddress()
            )
        )
        .map(this::toTransaction);
  }

  public Transaction<AccessRequestParams> toTransaction(AccessRequestParams accessRequestParams) {
    return Transaction.<AccessRequestParams>builder()
        .contractAddress(apiProperties.getBusinessDataContractAddress())
        .entryPoint(REQUEST_ACCESS_ENTRY_POINT)
        .entryPointParams(accessRequestParams)
        .build();
  }

  AccessRequestParams toAccessRequestParam(Integer tokenId, DatasourceDTO datasourceDTO,
      String providerAddress) {
    return AccessRequestParams.builder()
        .nftId(tokenId)
        .scopes(List.of(datasourceDTO.getId() + ":" + datasourceDTO.getAssetIdForDatasource()))
        .providerAddress(providerAddress)
        .build();
  }
}
