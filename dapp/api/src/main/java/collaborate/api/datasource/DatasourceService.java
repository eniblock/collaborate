package collaborate.api.datasource;

import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Keys.DATASOURCE_TYPE;
import static java.util.Collections.emptySet;

import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceDetailsDto;
import collaborate.api.datasource.model.dto.ListDatasourceDTO;
import collaborate.api.datasource.model.dto.enumeration.DatasourceStatus;
import collaborate.api.organization.OrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {

  private final AuthenticationService authenticationService;
  private final DatasourceRepository datasourceRepository;
  private final DatasourceMetadataService datasourceMetadataService;
  private final ObjectMapper objectMapper;
  private final OrganizationService organizationService;
  private final TraefikProviderService traefikProviderService;

  public Page<ListDatasourceDTO> findAllByCurrentOrg(Pageable pageable, String query) {
    log.warn("query={} not implemented", query);
    var currentOrgAddress = organizationService.getCurrentOrganization().getAddress();
    return datasourceRepository.findAllByOwner(currentOrgAddress, pageable)
        .map(d -> ListDatasourceDTO.builder()
            .creationDateTime(d.getCreationDatetime())
            .datasourceType(d.findMetadataByName(DATASOURCE_TYPE).orElse(""))
            .id(d.getId())
            .name(d.getName())
            .purpose(d.getPurpose(objectMapper))
            // FIXME
            .nbGrantedAccess(0)
            .status(DatasourceStatus.CREATED)
            .build())
        ;
  }

  public Optional<Datasource> findById(String id) {
    return datasourceRepository.findById(id);
  }

  public Optional<DatasourceDetailsDto> findDetailsById(String id) {
    return datasourceRepository.findById(id)
        .map(this::buildDatasourceDetailsDto);
  }

  private DatasourceDetailsDto buildDatasourceDetailsDto(Datasource datasource) {
    return DatasourceDetailsDto.builder()
        .id(datasource.getId())
        .name(datasource.getName())
        .purpose(datasourceMetadataService.getPurpose(datasource))
        .listOfScopes(
            getResourcesByDataSourceId(datasource.getId())
                .orElse(emptySet())
        ).baseURI(traefikProviderService.buildDatasourceBaseUri(datasource))
        .authenticationType(authenticationService.getAccessMethodName(datasource.getId()))
        .datasourceType(datasourceMetadataService.getType(datasource))
        .partnerTransferMethod(
            authenticationService.getTransferMethod(datasource.getId()))
        .build();
  }


  public Optional<Set<String>> getResourcesByDataSourceId(String datasourceId) {
    return getMetadata(datasourceId)
        .flatMap(
            metadata -> metadata.stream().filter(m -> "resources".equals(m.getName())).findFirst())
        .map(Metadata::getValue)
        .map(resources -> resources.split(","))
        .map(Arrays::asList)
        .map(l -> l.stream().map(String::strip).collect(Collectors.toList()))
        .map(HashSet::new);
  }

  public Optional<Set<Metadata>> getMetadata(String datasourceId) {
    return datasourceRepository.findById(datasourceId)
        .map(Datasource::getProviderMetadata);
  }

  public Datasource saveIfNotExists(Datasource d) {
    return datasourceRepository.findById(d.getId())
        .orElseGet(() -> datasourceRepository.save(d));
  }
}
