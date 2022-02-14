package collaborate.api.datasource.create;

import static collaborate.api.test.TestResources.objectMapper;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.DatasourceDAO;
import collaborate.api.datasource.TestConnectionVisitor;
import collaborate.api.datasource.businessdata.create.MintBusinessDataService;
import collaborate.api.datasource.gateway.SaveAuthenticationVisitor;
import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.gateway.traefik.model.TraefikProviderConfiguration;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.organization.OrganizationService;
import collaborate.api.organization.model.OrganizationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
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
  AuthenticationMetadataVisitor authenticationMetadataVisitor;
  @Mock
  DatasourceDAO datasourceDAO;
  @Mock
  DatasourceDTOMetadataVisitor datasourceDTOMetadataVisitor;
  @Mock
  OrganizationService organizationService;
  @Mock
  MintBusinessDataService mintBusinessDataService;
  @Mock
  SaveAuthenticationVisitor saveAuthenticationVisitor;
  @Mock
  TestConnectionVisitor testConnectionVisitor;
  @Mock
  TraefikProviderService traefikProviderService;
  @InjectMocks
  CreateDatasourceService createDatasourceService;

  @BeforeEach
  void setUp() {
    Clock clock = Clock.fixed(Instant.parse("2018-08-19T16:45:42.00Z"), ZoneOffset.UTC);
    createDatasourceService =
        new CreateDatasourceService(
            authenticationMetadataVisitor,
            datasourceDAO,
            datasourceDTOMetadataVisitor,
            objectMapper,
            organizationService,
            mintBusinessDataService,
            saveAuthenticationVisitor,
            testConnectionVisitor,
            traefikProviderService,
            uuidGenerator,
            clock);
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
        (LinkedHashMap<?, ?>) datasourceResult.getProviderConfiguration().get("middlewares");
    var serializedDatasourceResult = objectMapper.writeValueAsString(middlewareResult);
    assertThat(serializedDatasourceResult).doesNotContain(datasourceId + "-auth-headers");
  }

  @Test
  void buildMetadata_shouldContainsAuthenticationAndDatasourceMetadata()
      throws DatasourceVisitorException {
    // GIVEN
    var datasource = CertificateBasedBasicAuthDatasourceFeatures.datasource;
    var authMetadata = Metadata.builder()
        .name("authName")
        .value("authValue")
        .build();
    when(authenticationMetadataVisitor
        .visitCertificateBasedBasicAuth(
            (CertificateBasedBasicAuth) datasource.getAuthMethod())
    ).thenReturn(Stream.of(authMetadata));

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
        authMetadata,
        datasourceMetadata
    );
  }
}
