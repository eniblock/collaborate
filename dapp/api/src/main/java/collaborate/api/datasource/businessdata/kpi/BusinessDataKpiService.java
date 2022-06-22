package collaborate.api.datasource.businessdata.kpi;

import collaborate.api.datasource.create.DataCatalogCreationDTO;
import collaborate.api.datasource.kpi.Kpi;
import collaborate.api.datasource.kpi.KpiService;
import collaborate.api.datasource.kpi.KpiSpecification;
import collaborate.api.transaction.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessDataKpiService {

  public static final String DATASOURCE_ID = "datasourceId";
  private final ObjectMapper objectMapper;
  private final String businessDataContractAddress;
  private final KpiService kpiService;

  public void onDatasourceCreated(Transaction transaction) {
    var creationParams = getDataCatalogCreationDTOParams(transaction);
    String datasourceId = getDatasourceId(creationParams);
    var kpi = buildDatasourceCreatedKpi(transaction, datasourceId);

    var onMissingCondition = new KpiSpecification(DATASOURCE_ID, datasourceId);
    kpiService.saveIfValueMissing(kpi, onMissingCondition);
  }

  private String getDatasourceId(DataCatalogCreationDTO creationDTO) {
    return StringUtils.substringBefore(creationDTO.getAssetId(), ":");
  }

  private String getNftAlias(DataCatalogCreationDTO creationDTO) {
    return StringUtils.substringAfter(creationDTO.getAssetId(), ":");
  }

  private DataCatalogCreationDTO getDataCatalogCreationDTOParams(Transaction transaction) {
    try {
      return objectMapper.treeToValue(
          transaction.getParameters(),
          DataCatalogCreationDTO.class
      );
    } catch (JsonProcessingException e) {
      log.error(
          "While converting transactionParameters={} to DataCatalogCreationDTO",
          transaction.getParameters());
      throw new IllegalStateException(e);
    }
  }

  private Kpi buildDatasourceCreatedKpi(Transaction transaction, String datasourceId) {
    return Kpi.builder()
        .createdAt(transaction.getTimestamp())
        .kpiKey("datasource.created")
        .organizationWallet(transaction.getSource())
        .values(objectMapper.convertValue(Map.of(
            DATASOURCE_ID, datasourceId,
            "contract", businessDataContractAddress
        ), JsonNode.class))
        .build();
  }

  public void onScopeCreated(Transaction transaction) {
    var kpi = buildNFTKpi(transaction);
    kpiService.save(kpi);
  }

  private Kpi buildNFTKpi(Transaction transaction) {
    var creationParams = getDataCatalogCreationDTOParams(transaction);

    return Kpi.builder()
        .createdAt(transaction.getTimestamp())
        .kpiKey("nft.created")
        .organizationWallet(transaction.getSource())
        .values(objectMapper.convertValue(Map.of(
            DATASOURCE_ID, getDatasourceId(creationParams),
            "contract", businessDataContractAddress,
            "scope", getNftAlias(creationParams)
        ), JsonNode.class))
        .build();
  }

}
