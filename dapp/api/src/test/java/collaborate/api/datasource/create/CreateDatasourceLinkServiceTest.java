package collaborate.api.datasource.create;

import static collaborate.api.test.TestResources.objectMapper;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.config.UUIDGenerator;
import collaborate.api.datasource.DatasourceDAO;
import collaborate.api.datasource.create.provider.traefik.TraefikProviderService;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.datasource.model.traefik.TraefikProviderConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateDatasourceLinkServiceTest {

  private final UUID datasourceUUID = UUID.fromString("525003f6-f85f-11eb-9a03-0242ac130003");
  @Mock UUIDGenerator uuidGenerator;
  @Mock DatasourceDAO datasourceDAO;
  @Mock ProviderMetadataFactory providerMetadataFactory;
  @Mock TraefikProviderService traefikProviderService;
  @InjectMocks CreateDatasourceService createDatasourceService;

  @BeforeEach
  void setUp() {
    Clock clock = Clock.fixed(Instant.parse("2018-08-19T16:45:42.00Z"), ZoneOffset.UTC);
    createDatasourceService =
        new CreateDatasourceService(
            datasourceDAO,
            objectMapper,
            providerMetadataFactory,
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
                    CreateDatasourceLinkServiceTest.class.getResourceAsStream(
                        "/datasource/domain/traefik/entrypoint.yml")),
                UTF_8.name()),
            TraefikProviderConfiguration.class);
    assertThat(traefikConfiguration.getHttp().getMiddlewares().get(datasourceId + "-auth-headers"))
        .isNotNull();

    // WHEN

    var datasourceResult =
        createDatasourceService.buildDatasource(datasourceDTO, traefikConfiguration);
    // THEN
    var httpResult = (LinkedHashMap<?, ?>) datasourceResult.getProviderConfiguration().get("http");
    var middlewareResult =
        (LinkedHashMap<?, ?>) datasourceResult.getProviderConfiguration().get("middlewares");
    var serializedDatasourceResult = objectMapper.writeValueAsString(middlewareResult);
    assertThat(serializedDatasourceResult).doesNotContain(datasourceId + "-auth-headers");
  }
}