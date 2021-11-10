package collaborate.api.passport.metric;

import static collaborate.api.test.TestResources.readPath;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.gateway.GatewayUrlService;
import collaborate.api.passport.find.FindPassportService;
import collaborate.api.passport.model.AssetDataCatalogDTO;
import collaborate.api.passport.model.DatasourceDTO;
import collaborate.api.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetricServiceTest {

  public static final String traefikUrl = "https://localhost.test:4242";
  @Mock
  Clock clock;
  @Mock
  DatasourceService datasourceService;
  @Mock
  FindPassportService findPassportService;
  @Mock
  GatewayUrlService gatewayUrlService;
  @Mock
  TraefikProperties traefikProperties;
  ObjectMapper objectMapper = spy(TestResources.objectMapper);
  @InjectMocks
  MetricService metricService;

  @Test
  void buildMetricUrl_shouldResultInExpectedFormattedUrl_withoutEndingSlashInTraefikUrl() {
    // GIVEN
    DatasourceDTO datasourceDTO = DatasourceDTO.builder()
        .id("dsId")
        .assetIdForDatasource("assetId")
        .baseUri("unused")
        .scopes(Set.of("scope:metric:A", "scope:metric:B"))
        .build();
    when(traefikProperties.getUrl()).thenReturn(traefikUrl);
    when(datasourceService.getMetadata("dsId")).thenReturn(emptySet());
    // WHEN
    var metricUrl = metricService.buildMetricUrls(datasourceDTO);

    // THEN
    assertThat(metricUrl.collect(toList())).containsExactlyInAnyOrder(
        MetricGatewayDTO.builder()
            .scope("A")
            .uri(traefikUrl + "/datasource/dsId/scope:metric:A/assetId")
            .metadata(emptySet())
            .build(),
        MetricGatewayDTO.builder()
            .scope("B")
            .uri(traefikUrl + "/datasource/dsId/scope:metric:B/assetId")
            .metadata(emptySet())
            .build()
    );
  }

  @Test
  void buildMetricUrl_shouldResultInExpectedFormattedUrl_withEndingSlashInTraefikUrl() {
    // GIVEN
    when(traefikProperties.getUrl()).thenReturn(traefikUrl + "/");
    when(datasourceService.getMetadata("dsId")).thenReturn(emptySet());
    DatasourceDTO datasourceDTO = DatasourceDTO.builder()
        .id("dsId")
        .assetIdForDatasource("assetId")
        .baseUri("unused")
        .scopes(Set.of("scope:metric:A"))
        .build();
    // WHEN
    var metricUrl = metricService.buildMetricUrls(datasourceDTO);

    // THEN
    assertThat(metricUrl.collect(toList())).containsExactlyInAnyOrder(
        MetricGatewayDTO.builder()
            .scope("A")
            .uri(traefikUrl + "/datasource/dsId/scope:metric:A/assetId")
            .metadata(emptySet())
            .build()
    );
  }

  @Test
  void getMetricsAndUriStream_shouldResultInExpectedMergedUrlList_withMultipleDatasources() {
    // GIVEN
    when(traefikProperties.getUrl()).thenReturn(traefikUrl + "/");

    DigitalPassportDetailsDTO passportDetailsDTO = DigitalPassportDetailsDTO.builder()
        .assetDataCatalog(
            new AssetDataCatalogDTO(
                List.of(
                    DatasourceDTO.builder()
                        .id("dsA")
                        .assetIdForDatasource("assetIdA")
                        .baseUri("unused")
                        .scopes(Set.of("scope:metric:A"))
                        .build(),
                    DatasourceDTO.builder()
                        .id("dsB")
                        .assetIdForDatasource("assetIdB")
                        .baseUri("unused")
                        .scopes(Set.of("scope:metric:A", "scope:metric:B"))
                        .build()
                )
            )
        ).build();
    when(datasourceService.getMetadata("dsA")).thenReturn(emptySet());
    // WHEN
    var metricUrls = metricService.buildMetricUrls(passportDetailsDTO);
    // THEN
    assertThat(metricUrls.collect(toList())).containsExactlyInAnyOrder(
        MetricGatewayDTO.builder()
            .scope("A")
            .uri(traefikUrl + "/datasource/dsA/scope:metric:A/assetIdA")
            .metadata(emptySet())
            .build(),
        MetricGatewayDTO.builder()
            .scope("A")
            .uri(traefikUrl + "/datasource/dsB/scope:metric:A/assetIdB")
            .metadata(emptySet())
            .build(),
        MetricGatewayDTO.builder()
            .scope("B")
            .uri(traefikUrl + "/datasource/dsB/scope:metric:B/assetIdB")
            .metadata(emptySet())
            .build()
    );
  }

  @Test
  void getScopeMetadata_shouldFilterMetadataByScope() {
    // GIVEN
    Metadata purposeMetadata = Metadata.builder()
        .name("datasource:purpose")
        .value("[\"digital-passport\",\"vehicles\"]")
        .build();
    Metadata odometerJsonPathMetadata = Metadata.builder()
        .name("scope:metric:odometer:value.jsonPath")
        .value("$._embedded.odometer.value")
        .build();
    Metadata energyJsonPathMetadata = Metadata.builder()
        .name("scope:metric:energy:value.jsonPath")
        .value("$._embedded.energy.value")
        .build();

    Set<Metadata> metadata = Set.of(
        purposeMetadata,
        odometerJsonPathMetadata,
        energyJsonPathMetadata
    );
    String scope = "scope:metric:odometer";
    // WHEN
    var scopeMetadata = metricService.getScopeMetadata(scope, metadata);
    // THEN
    assertThat(scopeMetadata).containsExactlyInAnyOrder(
        Metadata.builder()
            .name("value.jsonPath")
            .value("$._embedded.odometer.value")
            .build()
    );
  }

  private static Stream<Arguments> extractValuePathParameters() {
    return Stream.of(
        Arguments.of("$.odometer.mileage", "5477.9"),
        Arguments.of("$.energies[?(@.type == 'Electric')].level[0]", "79"),
        Arguments.of("$.energies[?(@.type == 'hydrogen')].level[0]", "")
    );
  }

  @ParameterizedTest
  @MethodSource("extractValuePathParameters")
  void keepPath_shouldReturnExpected_withFieldPath(String path, String expectedResult) {
    // GIVEN
    Set<Metadata> metadata = Set.of(
        Metadata.builder()
            .name("value.jsonPath")
            .value(path)
            .build()
    );
    var jsonResponse = readPath("/passport/metric/metric.response.json");
    // WHEN
    var jsonNodeResult = metricService.extractValuePath(jsonResponse, metadata);
    // THEN
    assertThat(jsonNodeResult).isEqualTo(TestResources.objectMapper.valueToTree(expectedResult));
  }
}
