package collaborate.api.datasource.create;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;

import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.DatasourceDAO;
import collaborate.api.datasource.DatasourceProperties;
import collaborate.api.datasource.TestConnectionVisitor;
import collaborate.api.datasource.businessdata.create.MintBusinessDataService;
import collaborate.api.datasource.gateway.SaveAuthenticationVisitor;
import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.gateway.traefik.model.TraefikProviderConfiguration;
import collaborate.api.datasource.gateway.traefik.routing.AuthHeaderKeySupplier;
import collaborate.api.datasource.gateway.traefik.routing.DatasourceKeySupplier;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourcePurpose;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.ipfs.IpfsDAO;
import collaborate.api.organization.OrganizationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZonedDateTime;
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
  private final DatasourceDTOMetadataVisitor datasourceDTOMetadataVisitor;

  private final DatasourceDAO datasourceDAO;
  private final ObjectMapper objectMapper;
  private final OrganizationService organizationService;
  private final MintBusinessDataService mintBusinessDataService;
  private final SaveAuthenticationVisitor saveAuthenticationVisitor;
  private final TestConnectionVisitor testConnectionVisitor;
  private final TraefikProviderService traefikProviderService;
  private final UUIDGenerator uuidGenerator;
  private final Clock clock;

  private final DatasourceProperties datasourceProperties;
  private final DateFormatterFactory dateFormatterFactory;
  private final IpfsDAO ipfsDAO;

  public Datasource create(DatasourceDTO datasourceDTO, Optional<MultipartFile> pfxFile)
      throws DatasourceVisitorException, IOException {
    datasourceDTO = copyWithPfxFileContent(datasourceDTO, pfxFile);
    datasourceDTO.getAuthMethod().setDatasource(datasourceDTO);
    datasourceDTO.setId(uuidGenerator.randomUUID());

    datasourceDTO.getAuthMethod().accept(saveAuthenticationVisitor);
    var providerConfiguration = traefikProviderService.save(datasourceDTO);
    var datasource = buildDatasource(datasourceDTO, providerConfiguration);

    var savedDatasource = save(datasource);
    if (DatasourcePurpose.BUSINESS_DATA.match(datasourceDTO)) {
      mintBusinessDataService.mint(datasourceDTO);
    }
    return savedDatasource;
  }

  Datasource buildDatasource(DatasourceDTO datasourceDTO,
      TraefikProviderConfiguration providerConfiguration) throws DatasourceVisitorException {

    var authHeaderKeySupplier = new AuthHeaderKeySupplier(new DatasourceKeySupplier(datasourceDTO));
    providerConfiguration.getHttp().getMiddlewares().remove(authHeaderKeySupplier.get());

    return Datasource.builder()
        .id(datasourceDTO.getId().toString())
        .name(datasourceDTO.getName())
        .creationDatetime(ZonedDateTime.now(clock))
        .owner(organizationService.getCurrentOrganization().getAddress())
        .providerConfiguration(
            objectMapper.convertValue(providerConfiguration, JsonNode.class)
        ).provider(TraefikProviderConfiguration.class.getName())
        .providerMetadata(buildMetadata(datasourceDTO))
        .build();
  }

  Set<Metadata> buildMetadata(DatasourceDTO datasourceDTO)
      throws DatasourceVisitorException {
    return Stream.of(
            datasourceDTO.getAuthMethod().accept(authenticationMetadataVisitor),
            datasourceDTO.accept(datasourceDTOMetadataVisitor)
        ).flatMap(identity())
        .collect(toSet());
  }

  public Datasource save(Datasource datasource) throws IOException {
    var datasourcePath = Path.of(datasourceProperties.getRootFolder(),
        dateFormatterFactory.forPattern(datasourceProperties.getPartitionDatePattern()),
        datasource.getId()
    );
    var cid = ipfsDAO.add(datasourcePath, datasource);
    datasource.setCid(cid);
    datasourceDAO.save(datasource);
    return datasource;
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
