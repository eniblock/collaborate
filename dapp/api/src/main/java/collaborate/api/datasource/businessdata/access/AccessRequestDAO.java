package collaborate.api.datasource.businessdata.access;

import static collaborate.api.tag.TezosApiGatewayJobClient.ORGANIZATION_SECURE_KEY_NAME;
import static java.util.stream.Collectors.toList;

import collaborate.api.datasource.businessdata.access.model.AccessRequestDTO;
import collaborate.api.datasource.businessdata.access.model.AccessRequestParams;
import collaborate.api.config.UUIDGenerator;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.tag.TezosApiGatewayJobClient;
import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.job.Transaction;
import collaborate.api.tag.model.job.TransactionBatch;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
class AccessRequestDAO {

  public static final String REQUEST_ACCESS_ENTRY_POINT = "request_access";
  private final ApiProperties apiProperties;
  private final TezosApiGatewayJobClient tezosApiGatewayClient;
  private final UUIDGenerator uuidGenerator;

  public Job accessRequest(List<AccessRequestDTO> accessRequestDTOs) {
    var transactions = accessRequestDTOs.stream()
        .map(this::toAccessRequestParam)
        .map(this::toTransaction)
        .collect(toList());

    return tezosApiGatewayClient.sendTransactionBatch(
        new TransactionBatch<>(transactions, ORGANIZATION_SECURE_KEY_NAME),
        false
    );
  }


  AccessRequestParams toAccessRequestParam(AccessRequestDTO accessRequestDTO) {
    return AccessRequestParams.builder()
        .accessRequestsUuid(uuidGenerator.randomUUID())
        .nftId(accessRequestDTO.getTokenId())
        .scopes(List.of(
                buildScopeName(accessRequestDTO.getDatasourceId(),
                    accessRequestDTO.getAssetIdForDatasource())
            )
        ).providerAddress(accessRequestDTO.getProviderAddress())
        .build();
  }

  public Transaction<AccessRequestParams> toTransaction(AccessRequestParams accessRequestParams) {
    return Transaction.<AccessRequestParams>builder()
        .contractAddress(apiProperties.getBusinessDataContractAddress())
        .entryPoint(REQUEST_ACCESS_ENTRY_POINT)
        .entryPointParams(accessRequestParams)
        .build();
  }

  private String buildScopeName(String datasourceId, String assetIdForDatasource) {
    return datasourceId + ":" + assetIdForDatasource;
  }

}
