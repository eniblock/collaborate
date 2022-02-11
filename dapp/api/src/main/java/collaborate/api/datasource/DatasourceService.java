package collaborate.api.datasource;

import static java.util.Collections.emptySet;

import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.gateway.traefik.model.TraefikProviderConfiguration;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceDetailsDto;
import collaborate.api.datasource.model.dto.ListDatasourceDTO;
import collaborate.api.ipfs.domain.dto.ContentWithCid;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {

  private final ObjectMapper objectMapper;
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
            getScopesByDataSourceId(datasource.getId())
                .orElse(emptySet())
        ).baseURI(traefikProviderService.buildDatasourceBaseUri(datasource))
        .authenticationType(metadataService.getAuthentication(datasource))
        .datasourceType(metadataService.getType(datasource))
        .partnerTransferMethod(metadataService.getPartnerTransferMethod(datasource))
        .build();
  }


  public Optional<Set<String>> getScopesByDataSourceId(String datasourceId) {
    return datasourceDAO.findById(datasourceId)
        .map(ContentWithCid::getContent)
        .filter(ds -> TraefikProviderConfiguration.class.getName().equals(ds.getProvider()))
        .map(Datasource::getProviderConfiguration)
        .map(providerConfig ->
            objectMapper.convertValue(providerConfig, TraefikProviderConfiguration.class)
        ).map(conf -> conf.getHttp().getRouters().keySet())
        .map(
            scopeSet ->
                scopeSet.stream()
                    .map(s -> StringUtils.removeStart(s, datasourceId + "-"))
                    .map(s -> StringUtils.removeEnd(s, "-router"))
                    .filter(s -> StringUtils.startsWith(s, "scope:"))
                    .collect(Collectors.toSet())
        );
  }

  public Set<Metadata> getMetadata(String datasourceId) {
    return datasourceDAO.findById(datasourceId)
        .map(ContentWithCid::getContent)
        .map(Datasource::getProviderMetadata)
        .orElse(Collections.emptySet());
  }

}
