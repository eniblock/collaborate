package collaborate.api.datasource.gateway.datasource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.DatasourceController;
import collaborate.api.datasource.DatasourceService;
import collaborate.api.datasource.create.CreateDatasourceService;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceDetailsDto;
import collaborate.api.datasource.model.dto.web.OAuth2DatasourceFeatures;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class DatasourceControllerTest {

  UUID datasourceUUID = UUID.fromString("1fc84579-69fa-40bd-a4bd-b4b79139e53b");
  @Mock
  private DatasourceService datasourceService;
  @Mock
  private CreateDatasourceService createDatasourceService;
  @InjectMocks
  private DatasourceController datasourceController;

  @Test
  void list_shouldCallSearchService() {
    // GIVEN
    Pageable pageable = PageRequest.of(0, 20);
    String query = "";

    when(datasourceService.findAllByCurrentOrg(pageable, query)).thenReturn(null);
    // WHEN
    datasourceController.listDatasources(pageable, query);
    // THEN
    verify(datasourceService, times(1)).findAllByCurrentOrg(any(Pageable.class), anyString());
  }

  @Test
  void get_shouldReturnDatasource_withExistingDatasourceId() {
    // GIVEN
    var expectedDatasource = DatasourceDetailsDto.builder()
        .id(datasourceUUID.toString())
        .build();
    when(datasourceService.findDetailsById(datasourceUUID.toString()))
        .thenReturn(
            Optional.of(
                expectedDatasource
            )
        );
    // WHEN
    var actual = datasourceController
        .getDatasourceById(datasourceUUID);
    // THEN
    assertThat(actual.getBody()).isEqualTo(expectedDatasource);
  }

  @Test
  void create_shouldCallExpectedService_withOAuth2() throws Exception {
    // GIVEN
    DatasourceDTO datasource = OAuth2DatasourceFeatures.datasource;
    when(createDatasourceService.create(datasource, Optional.empty())).thenReturn(null);
    when(createDatasourceService.testConnection(datasource, Optional.empty())).thenReturn(true);
    // WHEN
    datasourceController.createDatasource(datasource, Optional.empty());
    // THEN
    verify(createDatasourceService, times(1)).create(datasource, Optional.empty());
    verify(createDatasourceService, times(1)).testConnection(datasource, Optional.empty());
  }

  @Test
  void getScopesByDataSourceId_shouldReturn200OK_withNonEmptyScopes() {
    // GIVEN
    var scopesOpt = Optional.of(Set.of("scope"));
    // WHEN
    when(datasourceService.getResourcesByDataSourceId(datasourceUUID.toString()))
        .thenReturn(scopesOpt);
    // THEN
    assertThat(
        datasourceController.listScopesByDatasourceId(datasourceUUID.toString()).getStatusCode())
        .isEqualTo(HttpStatus.OK);
  }

  @Test
  void getScopesByDataSourceId_shouldReturn404NotFound_withEmptyScopes() {
    // GIVEN
    var scopesOpt = Optional.<Set<String>>empty();
    // WHEN
    when(datasourceService.getResourcesByDataSourceId(datasourceUUID.toString()))
        .thenReturn(scopesOpt);
    // THEN
    assertThat(
        datasourceController.listScopesByDatasourceId(datasourceUUID.toString()).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
