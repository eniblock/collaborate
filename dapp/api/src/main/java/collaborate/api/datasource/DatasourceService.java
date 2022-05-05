package collaborate.api.datasource;

import static java.util.Collections.emptySet;

import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceDetailsDto;
import collaborate.api.datasource.model.dto.ListDatasourceDTO;
import collaborate.api.ipfs.domain.dto.ContentWithCid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {
  private final DatasourceDAO datasourceDAO;
  private final MetadataService metadataService;
  private final TraefikProviderService traefikProviderService;

  public Page<ListDatasourceDTO> findAll(Pageable pageable, String query) {
    log.warn("query={} and sort not implemented", query);
    return datasourceDAO.findAll(pageable);
  }

  public Optional<ContentWithCid<Datasource>> findById(String id) {
    return datasourceDAO.findById(id);
  }

  public Optional<DatasourceDetailsDto> findDetailsById(String id) {
    return datasourceDAO.findById(id)
        .map(ContentWithCid::getContent)
        .map(this::buildDatasourceDetailsDto);
  }

  private DatasourceDetailsDto buildDatasourceDetailsDto(Datasource datasource) {
    return DatasourceDetailsDto.builder()
        .id(datasource.getId())
        .name(datasource.getName())
        .purpose(metadataService.getPurpose(datasource))
        .listOfScopes(
            getResourcesByDataSourceId(datasource.getId())
                .orElse(emptySet())
        ).baseURI(traefikProviderService.buildDatasourceBaseUri(datasource))
        .authenticationType(metadataService.getAuthentication(datasource))
        .datasourceType(metadataService.getType(datasource))
        .partnerTransferMethod(metadataService.getPartnerTransferMethod(datasource))
        .build();
  }


  public Optional<Set<String>> getResourcesByDataSourceId(String datasourceId) {
    return getMetadata(datasourceId)
        .flatMap(metadata -> metadata.stream().filter(m -> "resources".equals(m.getName())).findFirst())
        .map(Metadata::getValue)
        .map(resources -> resources.split(","))
        .map(Arrays::asList)
        .map(HashSet::new);
  }

  public Optional<Set<Metadata>> getMetadata(String datasourceId) {
    return datasourceDAO.findById(datasourceId)
        .map(ContentWithCid::getContent)
        .map(Datasource::getProviderMetadata);
  }

}
