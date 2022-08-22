package collaborate.api.datasource.businessdata.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.AuthenticationService;
import collaborate.api.datasource.DatasourceMetadataService;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.businessdata.document.model.BusinessDataDocument;
import collaborate.api.datasource.businessdata.find.AssetDetailsService;
import collaborate.api.datasource.gateway.GatewayUrlService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.http.HttpClientFactory;
import collaborate.api.tag.TagService;
import collaborate.api.test.TestResources;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class AssetsServiceTest {

  @Mock
  AssetDetailsService assetDetailsService;
  @Mock
  AuthenticationService authenticationService;
  String businessDataContractAddress = "smartContractAddress";
  Clock clock = Clock.fixed(Instant.parse("2018-08-19T16:45:42.00Z"), ZoneOffset.UTC);
  @Mock
  DatasourceService datasourceService;
  @Mock
  DatasourceMetadataService datasourceMetadataService;
  @Mock
  GatewayUrlService gatewayUrlService;
  @Mock
  HttpClientFactory httpClientFactory;
  @Mock
  CatalogService catalogService;
  @Mock
  TagService tagService;
  @InjectMocks
  AssetsService assetsService;

  @BeforeEach
  void setUp() {
    assetsService = new AssetsService(
        assetDetailsService,
        authenticationService,
        businessDataContractAddress,
        catalogService,
        clock,
        datasourceService,
        datasourceMetadataService,
        gatewayUrlService,
        httpClientFactory,
        TestResources.objectMapper,
        tagService
    );
  }

  @Test
  void convertJsonToScopeAssetDTOs() {
    // GIVEN
    var assetListJsonString = TestResources.readContent(
        "/datasource/businessdata/document/asset-list.json");
    var datasourceId = "datasourceId";
    var mockDatasource = Mockito.mock(Datasource.class);
    var resourceAlias = "resourceAlias";
    when(datasourceService.findById(datasourceId))
        .thenReturn(Optional.of(mockDatasource));
    when(datasourceMetadataService.findByAlias(mockDatasource, resourceAlias)).thenReturn(
        new HashMap<>());
    // WHEN
    var scopeAssetsResult = assetsService.convertJsonToBusinessDataDocument(assetListJsonString,
        datasourceId, resourceAlias);
    // THEN
    assertThat(scopeAssetsResult).containsExactlyInAnyOrder(
        BusinessDataDocument.builder()
            .name("Airport, airline and route data")
            .type("MVP document")
            .synchronizedDate(ZonedDateTime.now(clock))
            .downloadLink(
                URI.create(
                    "https://datasource-dsp-a.fake-datasource.localhost/referentials/airports/download"))
            .build()
    );
  }

  @Test
  void convertJsonToScopeAssetDTOs_withMetadata() {
    // GIVEN
    var assetListJsonString = TestResources.readContent(
        "/datasource/businessdata/document/asset-list.custom-structure.json");
    var datasourceId = "datasourceId";
    var mockDatasource = Mockito.mock(Datasource.class);
    var resourceAlias = "resourceAlias";
    when(datasourceService.findById(datasourceId))
        .thenReturn(Optional.of(mockDatasource));
    when(datasourceMetadataService.findByAlias(mockDatasource, resourceAlias)).thenReturn(
        Map.of("id.jsonPath", "$['key']",
            "downloadLink", "https://custom.api/$id"
        )
    );
    // WHEN
    var scopeAssetsResult = assetsService.convertJsonToBusinessDataDocument(assetListJsonString,
        datasourceId, resourceAlias);
    // THEN
    assertThat(scopeAssetsResult).containsExactlyInAnyOrder(
        BusinessDataDocument.builder()
            .name("2022-05-01LHRBA874B")
            .type("MVP document")
            .synchronizedDate(ZonedDateTime.now(clock))
            .downloadLink(
                URI.create(
                    "https://custom.api/2022-05-01LHRBA874B"))
            .build()
    );
  }
}
