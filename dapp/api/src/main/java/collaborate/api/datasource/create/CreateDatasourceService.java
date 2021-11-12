package collaborate.api.datasource.create;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;

import collaborate.api.businessdata.create.MintBusinessDataService;
import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.DatasourceDAO;
import collaborate.api.datasource.TestConnectionVisitor;
import collaborate.api.datasource.create.provider.traefik.TraefikProviderService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceEnrichment;
import collaborate.api.datasource.model.dto.DatasourcePurpose;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import collaborate.api.datasource.security.SaveAuthenticationVisitor;
import collaborate.api.datasource.traefik.routing.AuthHeaderKeySupplier;
import collaborate.api.datasource.traefik.routing.DatasourceKeySupplier;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class CreateDatasourceService {

  private final AuthenticationMetadataVisitor authenticationMetadataVisitor;
  private final DatasourceDAO datasourceDAO;
  private final DatasourceDTOMetadataVisitor datasourceDTOMetadataVisitor;
  private final DatasourceEnricherVisitor datasourceEnricherVisitor;
  private final ObjectMapper objectMapper;
  private final MintBusinessDataService mintBusinessDataService;
  private final SaveAuthenticationVisitor saveAuthenticationVisitor;
  private final TestConnectionVisitor testConnectionVisitor;
  private final TraefikProviderService traefikProviderService;
  private final UUIDGenerator uuidGenerator;
  private final Clock clock;

  public Datasource create(DatasourceDTO datasourceDTO, Optional<MultipartFile> pfxFile)
      throws DatasourceVisitorException, IOException {
    datasourceDTO = copyWithPfxFileContent(datasourceDTO, pfxFile);
    datasourceDTO.getAuthMethod().setDatasource(datasourceDTO);
    datasourceDTO.setId(uuidGenerator.randomUUID());

    datasourceDTO.getAuthMethod().accept(saveAuthenticationVisitor);
    var enrichment = datasourceDTO.accept(datasourceEnricherVisitor);
    var providerConfiguration = traefikProviderService.save(enrichment.getDatasource());
    var datasource = buildDatasource(enrichment, providerConfiguration);

    var datasourceWithCid = datasourceDAO.save(datasource);
    if (DatasourcePurpose.BUSINESS_DATA.match(datasourceDTO)) {
      mintBusinessDataService.mint(datasourceDTO);
    }
    return datasourceWithCid.getContent();
  }

  Datasource buildDatasource(
      DatasourceEnrichment<?> enrichment,
      TraefikProviderConfiguration providerConfiguration
  ) throws DatasourceVisitorException {

    var datasourceDTO = enrichment.getDatasource();
    var authHeaderKeySupplier = new AuthHeaderKeySupplier(new DatasourceKeySupplier(datasourceDTO));
    providerConfiguration.getHttp().getMiddlewares().remove(authHeaderKeySupplier.get());

    return Datasource.builder()
        .id(datasourceDTO.getId().toString())
        .name(datasourceDTO.getName())
        .creationDatetime(ZonedDateTime.now(clock))
        .providerConfiguration(
            objectMapper.convertValue(providerConfiguration, LinkedHashMap.class)
        ).provider(TraefikProviderConfiguration.class.getName())
        .providerMetadata(buildMetadata(enrichment))
        .build();
  }

  Set<Metadata> buildMetadata(DatasourceEnrichment<?> enrichment)
      throws DatasourceVisitorException {
    var datasourceDTO = enrichment.getDatasource();
    return Stream.of(
            enrichment.getMetadata().stream(),
            datasourceDTO.getAuthMethod().accept(authenticationMetadataVisitor),
            datasourceDTO.accept(datasourceDTOMetadataVisitor)
        ).flatMap(identity())
        .collect(toSet());
  }

  public boolean testConnection(DatasourceDTO datasource, Optional<MultipartFile> pfxFile)
      throws DatasourceVisitorException, IOException {
    BooleanSupplier connectionTester = copyWithPfxFileContent(datasource, pfxFile)
        .accept(testConnectionVisitor);
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
}
