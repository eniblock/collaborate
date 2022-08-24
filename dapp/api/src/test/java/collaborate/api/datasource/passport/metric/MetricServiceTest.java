package collaborate.api.datasource.passport.metric;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.gateway.GatewayResourceDTO;
import collaborate.api.datasource.gateway.GatewayUrlService;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.nft.model.AssetDataCatalogDTO;
import collaborate.api.datasource.nft.model.AssetDetailsDatasourceDTO;
import collaborate.api.datasource.passport.find.FindPassportService;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
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
  ObjectMapper objectMapper = spy(TestResources.objectMapper);
  @InjectMocks
  MetricService metricService;

  @Test
  void buildMetricUrl_shouldResultInExpectedFormattedUrl_withoutEndingSlashInTraefikUrl() {
    // GIVEN
    AssetDetailsDatasourceDTO datasourceDTO = AssetDetailsDatasourceDTO.builder()
        .id("dsId")
        .assetIdForDatasource("assetId")
        .baseUri("unused")
        .scopes(Set.of("scope:metric:A", "scope:metric:B"))
        .build();
    when(datasourceService.getMetadata("dsId")).thenReturn(Optional.empty());
    // WHEN
    var metricUrl = metricService.buildMetricUrls(datasourceDTO);

    // THEN
    assertThat(metricUrl.collect(toList())).containsExactlyInAnyOrder(
        GatewayResourceDTO.builder()
            .alias("scope:metric:A")
            .datasourceId("dsId")
            .assetIdForDatasource("assetId")
            .metadata(emptySet())
            .build(),
        GatewayResourceDTO.builder()
            .alias("scope:metric:B")
            .datasourceId("dsId")
            .assetIdForDatasource("assetId")
            .metadata(emptySet())
            .build()
    );
  }

  @Test
  void buildMetricUrl_shouldResultInExpectedFormattedUrl_withEndingSlashInTraefikUrl() {
    // GIVEN
    when(datasourceService.getMetadata("dsId")).thenReturn(Optional.empty());
    AssetDetailsDatasourceDTO datasourceDTO = AssetDetailsDatasourceDTO.builder()
        .id("dsId")
        .assetIdForDatasource("assetId")
        .baseUri("unused")
        .scopes(Set.of("scope:metric:A"))
        .build();
    // WHEN
    var metricUrl = metricService.buildMetricUrls(datasourceDTO);

    // THEN
    assertThat(metricUrl.collect(toList())).containsExactlyInAnyOrder(
        GatewayResourceDTO.builder()
            .datasourceId("dsId")
            .alias("scope:metric:A")
            .assetIdForDatasource("assetId")
            .metadata(emptySet())
            .build()
    );
  }

  @Test
  void getMetricsAndUriStream_shouldResultInExpectedMergedUrlList_withMultipleDatasources() {
    // GIVEN
    DigitalPassportDetailsDTO passportDetailsDTO = DigitalPassportDetailsDTO.builder()
        .assetDataCatalog(
            new AssetDataCatalogDTO(
                List.of(
                    AssetDetailsDatasourceDTO.builder()
                        .id("dsA")
                        .assetIdForDatasource("assetIdA")
                        .baseUri("unused")
                        .scopes(Set.of("scope:metric:A"))
                        .build(),
                    AssetDetailsDatasourceDTO.builder()
                        .id("dsB")
                        .assetIdForDatasource("assetIdB")
                        .baseUri("unused")
                        .scopes(Set.of(
                            "scope:metric:A",
                            "scope:metric:B")
                        )
                        .build()
                )
            )
        ).build();
    when(datasourceService.getMetadata("dsA")).thenReturn(Optional.empty());
    // WHEN
    var metricUrls = metricService.buildMetricUrls(passportDetailsDTO);
    // THEN
    assertThat(metricUrls.collect(toList())).containsExactlyInAnyOrder(
        GatewayResourceDTO.builder()
            .alias("scope:metric:A")
            .datasourceId("dsA")
            .assetIdForDatasource("assetIdA")
            .metadata(emptySet())
            .build(),
        GatewayResourceDTO.builder()
            .alias("scope:metric:A")
            .datasourceId("dsB")
            .assetIdForDatasource("assetIdB")
            .metadata(emptySet())
            .build(),
        GatewayResourceDTO.builder()
            .alias("scope:metric:B")
            .datasourceId("dsB")
            .assetIdForDatasource("assetIdB")
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
    var jsonResponse = TestResources.readContent(
        "/datasource/passport/metric/metric.response.json");
    // WHEN
    var jsonNodeResult = metricService.extractValuePath(jsonResponse, metadata);
    // THEN
    assertThat(jsonNodeResult).isEqualTo(TestResources.objectMapper.valueToTree(expectedResult));
  }
}
