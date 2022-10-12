package collaborate.api.datasource.create;

import static collaborate.api.test.TestResources.objectMapper;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.DatasourceProperties;
import collaborate.api.datasource.DatasourceRepository;
import collaborate.api.datasource.TestConnectionVisitor;
import collaborate.api.datasource.businessdata.create.MintBusinessDataService;
import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.gateway.traefik.model.TraefikProviderConfiguration;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.date.DateFormatterFactory;
import collaborate.api.ipfs.IpfsDAO;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateDatasourceServiceTest {

  @Mock
  UUIDGenerator uuidGenerator;
  @Mock
  AuthenticationService authenticationService;
  @Mock
  DatasourceRepository datasourceRepository;
  @Mock
  DatasourceDTOMetadataVisitor datasourceDTOMetadataVisitor;
  @Mock
  OrganizationService organizationService;
  @Mock
  MintBusinessDataService mintBusinessDataService;
  @Mock
  TestConnectionVisitor testConnectionVisitor;
  @Mock
  TraefikProviderService traefikProviderService;
  @Mock
  DatasourceProperties datasourceProperties;
  @Mock
  DateFormatterFactory dateFormatterFactory;
  @Mock
  IpfsDAO ipfsDAO;
  @InjectMocks
  CreateDatasourceService createDatasourceService;

  @BeforeEach
  void setUp() {
    Clock clock = Clock.fixed(Instant.parse("2018-08-19T16:45:42.00Z"), ZoneOffset.UTC);
    createDatasourceService =
        new CreateDatasourceService(
            authenticationService,
            datasourceDTOMetadataVisitor,
            datasourceRepository,
            objectMapper,
            organizationService,
            mintBusinessDataService,
            testConnectionVisitor,
            traefikProviderService,
            uuidGenerator,
            clock,
            datasourceProperties,
            dateFormatterFactory,
            ipfsDAO);
  }

  @Test
  void createDatasourceIpfsFile_shouldRemoveBasicAuthCredentialsToIpfs() throws Exception {
    // GIVEN
    DatasourceDTO datasourceDTO = CertificateBasedBasicAuthDatasourceFeatures.datasource;

    var datasourceId = datasourceDTO.getId().toString();
    var mapper = new ObjectMapper(new YAMLFactory());
    var traefikConfiguration =
        mapper.readValue(
            IOUtils.toString(
                Objects.requireNonNull(
                    CreateDatasourceServiceTest.class.getResourceAsStream(
                        "/datasource/gateway/traefik/entrypoint.yml")),
                UTF_8.name()),
            TraefikProviderConfiguration.class);
    assertThat(traefikConfiguration.getHttp().getMiddlewares().get(datasourceId + "-auth-headers"))
        .isNotNull();
    when(organizationService.getCurrentOrganization())
        .thenReturn(
            OrganizationDTO.builder()
                .address("dsOwnerAddress")
                .build()
        );
    // WHEN
    var datasourceResult =
        createDatasourceService.buildDatasource(
            datasourceDTO,
            traefikConfiguration);
    // THEN
    var middlewareResult =
        datasourceResult.getProviderConfiguration().get("middlewares");
    var serializedDatasourceResult = objectMapper.writeValueAsString(middlewareResult);
    assertThat(serializedDatasourceResult).doesNotContain(datasourceId + "-auth-headers");
  }

  @Test
  void buildMetadata_shouldContainsDatasourceMetadata()
      throws DatasourceVisitorException {
    // GIVEN
    var datasource = CertificateBasedBasicAuthDatasourceFeatures.datasource;

    var datasourceMetadata = Metadata.builder()
        .name("dsName")
        .value("dsValue")
        .build();
    when(datasourceDTOMetadataVisitor.visitWebServerDatasource(datasource))
        .thenReturn(Stream.of(datasourceMetadata));
    // WHEN
    var metadataResult = createDatasourceService.buildMetadata(datasource);
    // THEN
    assertThat(metadataResult).containsExactlyInAnyOrder(
        datasourceMetadata
    );
  }
}
