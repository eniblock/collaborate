package collaborate.api.datasource;

import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Keys.DATASOURCE_PURPOSE;

import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.ListDatasourceDTO;
import collaborate.api.datasource.model.dto.enumeration.DatasourceStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ListDatasourceDTOFactory {

  private final ObjectMapper objectMapper;

  public ListDatasourceDTO create(Datasource datasourceDto) {
    ListDatasourceDTO listDatasourceDTOResult = null;
    try {
      listDatasourceDTOResult = ListDatasourceDTO.builder()
          .id(datasourceDto.getId())
          .name(datasourceDto.getName())
          .creationDate(datasourceDto.getCreationDatetime().toString())
          .datasourceType(datasourceDto.getType())
          .status(DatasourceStatus.CREATED)
          .purpose(
              datasourceDto.getProviderMetadata().stream()
                  .filter(m -> DATASOURCE_PURPOSE.equals(m.getName()))
                  .map(Metadata::getValue)
                  .map(
                      v -> {
                        try {
                          return objectMapper.readValue(v, new TypeReference<>() {
                          });
                        } catch (JsonProcessingException e) {
                          log.error(
                              "Can't deserialize value={} as List of String", v);
                          return Collections.<String>emptyList();
                        }
                      })
                  .map(s -> s.stream().findFirst())
                  .flatMap(Optional::stream)
                  .findFirst()
                  .orElse(""))
          .build();
    } catch (Exception e) {
      log.error("while mapping datasourceDto={}, exception={}", datasourceDto, e);
    }
    return listDatasourceDTOResult;
  }
}
