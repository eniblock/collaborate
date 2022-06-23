package collaborate.api.datasource.gateway.datasource;

import static collaborate.api.test.TestResources.objectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.DatasourceDAO;
import collaborate.api.datasource.DatasourceMetadataService;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.gateway.traefik.TraefikProviderService;
import collaborate.api.datasource.gateway.traefik.model.Http;
import collaborate.api.datasource.gateway.traefik.model.Router;
import collaborate.api.datasource.gateway.traefik.model.TraefikProviderConfiguration;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.Metadata;
import collaborate.api.organization.OrganizationService;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatasourceServiceTest {

  @Mock
  DatasourceDAO datasourceDAO;
  @Mock
  DatasourceMetadataService datasourceMetadataService;
  @Mock
  OrganizationService organizationService;
  @Mock
  TraefikProviderService traefikProviderService;

  @InjectMocks
  private DatasourceService datasourceService;

  @BeforeEach
  void setUp() {
    datasourceService = new DatasourceService(
        datasourceDAO,
        datasourceMetadataService,
        objectMapper,
        organizationService,
        traefikProviderService
    );
  }

  @Test
  void getScopesByDataSourceId_shouldReturnEmpty_withProviderNotTraefik() {
    // GIVEN
    String datasourceId = "4f4daa53-eb12-4deb-b263-04b0e537842f";
    when(datasourceDAO.findById(datasourceId)).thenReturn(
        Optional.of(
            Datasource.builder()
                .provider("collaborate.api.datasource.gateway.datasource.Unknown")
                .build()
        )
    );
    // WHEN
    var scopesResult = datasourceService.getResourcesByDataSourceId(datasourceId);
    // THEN
    assertThat(scopesResult).isNotPresent();
  }

  @Test
  void getScopesByDataSourceId_shouldReturnEmpty_withDatasourceNotFound() {
    // GIVEN
    String datasourceId = "4f4daa53-eb12-4deb-b263-04b0e537842f";
    when(datasourceDAO.findById(datasourceId)).thenReturn(Optional.empty());
    // WHEN
    var scopesResult = datasourceService.getResourcesByDataSourceId(datasourceId);
    // THEN
    assertThat(scopesResult).isNotPresent();
  }

  @Test
  void getScopesByDataSourceId_shouldReturnEmptySet_withDatasourceNotContainingAnyScope() {
    // GIVEN
    String datasourceId = "4f4daa53-eb12-4deb-b263-04b0e537842f";
    var provider = objectMapper.convertValue(
        TraefikProviderConfiguration.builder()
            .http(Http.builder().build())
            .build(),
        JsonNode.class
    );
    when(datasourceDAO.findById(datasourceId)).thenReturn(
        Optional.of(
            Datasource.builder()
                .provider(TraefikProviderConfiguration.class.getName())
                .providerConfiguration(provider)
                .build()
        )
    );
    // WHEN
    var scopesResult = datasourceService.getResourcesByDataSourceId(datasourceId);
    // THEN
    assertThat(scopesResult).isNotPresent();
  }

  @Test
  void getResourcesByDataSourceId_shouldReturnExpectedScopes_withDatasourceContainingScope() {
    // GIVEN
    String datasourceId = "4f4daa53-eb12-4deb-b263-04b0e537842f";
    var provider = objectMapper.convertValue(
        TraefikProviderConfiguration.builder()
            .http(Http.builder()
                .routers(Map.of(
                    datasourceId + "-scope:odometer-router",
                    new Router(),
                    datasourceId + "-scope:energy:fuel-router",
                    new Router()
                ))
                .build())
            .build(),
        JsonNode.class
    );
    when(datasourceDAO.findById(datasourceId)).thenReturn(
        Optional.of(
            Datasource.builder()
                .provider(TraefikProviderConfiguration.class.getName())
                .providerMetadata(Set.of(
                    Metadata.builder()
                        .name("resources")
                        .value("odometer,fuel")
                        .build()
                ))
                .providerConfiguration(provider)
                .build()
        )
    );
    // WHEN
    var scopesResult = datasourceService.getResourcesByDataSourceId(datasourceId);
    // THEN
    assertThat(scopesResult).isPresent();
    assertThat(scopesResult.get())
        .containsExactlyInAnyOrder("odometer", "fuel");
  }
}

