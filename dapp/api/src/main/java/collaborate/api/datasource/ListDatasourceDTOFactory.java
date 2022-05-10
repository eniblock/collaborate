package collaborate.api.datasource;

import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.ListDatasourceDTO;
import collaborate.api.datasource.model.dto.enumeration.DatasourceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ListDatasourceDTOFactory {

  private final DatasourceMetadataService datasourceMetadataService;

  public ListDatasourceDTO create(Datasource datasource) {
    ListDatasourceDTO listDatasourceDTOResult = null;
    try {
      listDatasourceDTOResult = ListDatasourceDTO.builder()
          .creationDateTime(datasource.getCreationDatetime())
          .datasourceType(datasourceMetadataService.getType(datasource))
          .id(datasource.getId())
          .name(datasource.getName())
          .purpose(datasourceMetadataService.getPurpose(datasource))
          .status(DatasourceStatus.CREATED)
          .build();
    } catch (Exception e) {
      log.error("while mapping datasource={}, exception={}", datasource, e);
    }
    return listDatasourceDTOResult;
  }
}
