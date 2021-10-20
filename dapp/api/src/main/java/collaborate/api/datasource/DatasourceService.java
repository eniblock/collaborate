package collaborate.api.datasource;

import static java.util.Collections.emptySet;
import static java.util.function.Function.identity;

import collaborate.api.datasource.create.CreateDatasourceService;
import collaborate.api.datasource.model.Attribute;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceDetailsDto;
import collaborate.api.datasource.model.dto.ListDatasourceDTO;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import collaborate.api.http.security.SSLContextException;
import collaborate.api.ipfs.domain.dto.ContentWithCid;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class DatasourceService {

  private final ObjectMapper objectMapper;
  private final CreateDatasourceService createDatasourceService;
  private final DatasourceDAO datasourceDAO;
  private final TestConnectionFactory testConnectionFactory;

  public Datasource create(DatasourceDTO datasource, Optional<MultipartFile> pfxFile)
      throws Exception {
    datasource = copyWithPfxFileContent(datasource, pfxFile);
    return createDatasourceService.create(datasource);
  }

  public boolean testConnection(DatasourceDTO datasource, Optional<MultipartFile> pfxFile)
      throws UnrecoverableKeyException, SSLContextException, IOException {
    BooleanSupplier connectionTester =
        testConnectionFactory.create(copyWithPfxFileContent(datasource, pfxFile));
    return connectionTester.getAsBoolean();
  }

  private DatasourceDTO copyWithPfxFileContent(
      DatasourceDTO datasource, Optional<MultipartFile> pfxFile) throws IOException {
    if (pfxFile.isPresent() && datasource.getAuthMethod() instanceof CertificateBasedBasicAuth) {
      datasource =
          objectMapper.readValue(objectMapper.writeValueAsString(datasource), DatasourceDTO.class);
      ((CertificateBasedBasicAuth) datasource.getAuthMethod())
          .setPfxFileContent(pfxFile.get().getBytes());
    }
    return datasource;
  }

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
        .listOfScopes(
            getScopesByDataSourceId(datasource.getId())
                .orElse(emptySet())
        ).baseURI(buildDatasourceBaseUri(datasource))
        // FIXME to make it dynamic
        .AuthenticationType("Basic Authentication")
        // FIXME to make it dynamic
        .certificateEmail("ca@dsp.com")
        // FIXME to make it dynamic
        .accessMethod("Basic Authentication")
        // FIXME to make it dynamic
        .datasourceType("Web Server API")
        .build();
  }

  public String buildDatasourceBaseUri(Datasource datasource) {
    return toTraefikProviderConfiguration(datasource)
        .map(conf -> conf.getHttp().findFirstServiceLoadBalancerUri())
        .flatMap(identity())
        .orElse("");
  }

  public Optional<TraefikProviderConfiguration> toTraefikProviderConfiguration(
      Datasource datasource) {
    var isTraefikProviderConfiguration = TraefikProviderConfiguration.class
        .getName()
        .equals(datasource.getProvider());
    if (!isTraefikProviderConfiguration) {
      return Optional.empty();
    } else {
      return Optional.of(
          objectMapper.convertValue(
              datasource.getProviderConfiguration(),
              TraefikProviderConfiguration.class
          )
      );
    }
  }

  public Optional<Set<String>> getScopesByDataSourceId(String id) {
    return datasourceDAO.findById(id)
        .map(ContentWithCid::getContent)
        .filter(ds -> TraefikProviderConfiguration.class.getName().equals(ds.getProvider()))
        .map(Datasource::getProviderConfiguration)
        .map(providerConfig ->
            objectMapper.convertValue(providerConfig, TraefikProviderConfiguration.class)
        ).map(conf -> conf.getHttp().getRouters().keySet())
        .map(
            scopeSet ->
                scopeSet.stream()
                    .map(s -> StringUtils.removeStart(s, id + "-"))
                    .map(s -> StringUtils.removeEnd(s, "-router"))
                    .filter(s -> StringUtils.startsWith(s, "scope:"))
                    .collect(Collectors.toSet())
        );
  }

  public Set<Attribute> getMetadata(String datasourceId) {
    return datasourceDAO.findById(datasourceId)
        .map(ContentWithCid::getContent)
        .map(Datasource::getProviderMetadata)
        .orElse(Collections.emptySet());
  }
}
