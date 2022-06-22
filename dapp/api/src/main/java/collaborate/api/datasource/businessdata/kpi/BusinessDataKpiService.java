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

  private final ObjectMapper objectMapper;
  private final String businessDataContractAddress;
  private final KpiService kpiService;

  public void onDatasourceCreated(Transaction transaction) {
    String datasourceId = getDatasourceId(transaction);
    var kpi = buildDatasourceCreatedKpi(transaction, datasourceId);

    var onMissingCondition = new KpiSpecification("datasourceId", datasourceId);
    kpiService.saveIfValueMissing(kpi, onMissingCondition);
  }

  private String getDatasourceId(Transaction transaction) {
    var dataCatalog = getDataCatalogCreationDTOParams(transaction);
    return StringUtils.substringBefore(dataCatalog.getAssetId(), ":");
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
            "datasourceId", datasourceId,
            "contract", businessDataContractAddress
        ), JsonNode.class))
        .build();
  }

  public void onScopeCreated(Transaction transaction) {
    String datasourceId = getDatasourceId(transaction);
    var kpi = buildDatasourceCreatedKpi(transaction, datasourceId);
    kpiService.save(kpi);
  }

}
