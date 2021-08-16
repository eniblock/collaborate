package collaborate.api.datasource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.domain.Datasource;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.domain.web.authentication.OAuth2;
import collaborate.api.http.security.SSLContextException;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DatasourceControllerTest {

  @Mock
  private DatasourceService datasourceService;

  @InjectMocks
  private DatasourceController datasourceController;

  UUID datasourceUUID = UUID.fromString("1fc84579-69fa-40bd-a4bd-b4b79139e53b");

  @Test
  void list_shouldCallSearchService() {
    //GIVEN
    List<Datasource> list = new ArrayList<>();
    Pageable pageable = PageRequest.of(0, 20);
    String query = "";
    Page<Datasource> datasourcePage = new PageImpl<>(list, pageable, 0);
    
    when(datasourceService.search(pageable, query)).thenReturn(datasourcePage);
    //WHEN
    datasourceController.list(pageable, query);
    //THEN
    verify(datasourceService, times(1)).search(any(Pageable.class), anyString());
  }

  @Test
  void get_shouldReturnDatasource_withExistingDatasourceId() {
    //GIVEN
    Datasource expectedDatasource = WebServerDatasource.builder()
        .id(datasourceUUID)
        .build();
    when(datasourceService.findById(datasourceUUID)).thenReturn(Optional.of(expectedDatasource));
    //WHEN
    ResponseEntity<Datasource> actual = datasourceController.getById(datasourceUUID);
    //THEN
    assertThat(actual.getBody()).isEqualTo(expectedDatasource);
  }

  @Test
  void create_shouldCallExpectedService_withOAuth2()
      throws SSLContextException, IOException, UnrecoverableKeyException {
    //GIVEN
    Datasource datasource = WebServerDatasource.builder()
        .id(UUID.fromString("1fc84579-69fa-40bd-a4bd-b4b79139e53b"))
        .authMethod(new OAuth2())
        .build();
    when(datasourceService.create(datasource)).thenReturn(datasource);
    when(datasourceService.testConnection(datasource, Optional.empty())).thenReturn(true);
    //WHEN
    datasourceController.create(datasource, Optional.empty());
    //THEN
    verify(datasourceService, times(1)).create(datasource);
    verify(datasourceService, times(1)).testConnection(datasource, Optional.empty());
  }

}
