package collaborate.api.passport.metric;

import static collaborate.api.test.TestResources.readPath;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.TraefikProperties;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.model.Attribute;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    Attribute purposeAttribute = Attribute.builder()
        .name("datasource:purpose")
        .value("[\"digital-passport\",\"vehicles\"]")
        .build();
    Attribute odometerJsonPathAttribute = Attribute.builder()
        .name("scope:metric:odometer:value.jsonPath")
        .value("$._embedded.odometer.value")
        .build();
    Attribute energyJsonPathAttribute = Attribute.builder()
        .name("scope:metric:energy:value.jsonPath")
        .value("$._embedded.energy.value")
        .build();

    Set<Attribute> metadata = Set.of(
        purposeAttribute,
        odometerJsonPathAttribute,
        energyJsonPathAttribute
    );
    String scope = "scope:metric:odometer";
    // WHEN
    var scopeMetadata = metricService.getScopeMetadata(scope, metadata);
    // THEN
    assertThat(scopeMetadata).containsExactlyInAnyOrder(
        Attribute.builder()
            .name("value.jsonPath")
            .value("$._embedded.odometer.value")
            .build()
    );
  }

  @Test
  void keepPath() {
    // GIVEN
    Set<Attribute> metadata = Set.of(
        Attribute.builder()
            .name("value.jsonPath")
            .value("$.odometer.mileage")
            .build()
    );
    var jsonResponse = readPath("/passport/metric/metric.response.json");
    // WHEN
    var jsonNodeResult = metricService.extractValuePath(jsonResponse, metadata);
    // THEN
    assertThat(jsonNodeResult).isEqualTo(TestResources.objectMapper.valueToTree("5477.9"));
  }
}
