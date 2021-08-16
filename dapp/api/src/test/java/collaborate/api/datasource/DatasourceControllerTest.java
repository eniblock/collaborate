package collaborate.api.datasource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


import collaborate.api.datasource.domain.DataSource;
import collaborate.api.datasource.domain.authentication.Authentication;
import collaborate.api.datasource.domain.authentication.Oauth;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.http.security.SSLContextException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DatasourceControllerTest {

  @Mock
  private DatasourceService datasourceService;

  @InjectMocks
  private DatasourceController datasourceController;

  @Test
  void list() {
    //GIVEN
    List<DataSource> list = new ArrayList<>();
    Pageable pageable = PageRequest.of(0, 20);
    String query = "";
    Page<DataSource> datasourcePage = new PageImpl<>(list, pageable, list.size());
    when(datasourceService.search(pageable, query)).thenReturn(datasourcePage);
    //WHEN
    HttpEntity<Page<DataSource>> actual = datasourceController.list(pageable, query);
    //THEN
    verify(datasourceService, times(1)).search(any(Pageable.class), anyString());
  }

  @Test
  void get() {
    //GIVEN
    DataSource datasource = WebServerDatasource.builder().id(1L).build();
    when(datasourceService.findById(anyLong())).thenReturn(datasource);
    //WHEN
    ResponseEntity<DataSource> actual = datasourceController.get(1L);
    //THEN
    assertThat(actual.getBody().getId()).isEqualTo(datasource.getId());
  }

  @Test
  void create_OAuth_datasource() throws SSLContextException, IOException {
    //GIVEN
    Authentication oauth = new Oauth();
    DataSource datasource = WebServerDatasource.builder().id(1L).authMethod(oauth).build();
    when(datasourceService.create(any(DataSource.class))).thenReturn(datasource);
    //WHEN
    datasourceController.createWithOAuth(datasource);
    //THE
    verify(datasourceService, times(1)).create(any(DataSource.class));

  }

  @Test
  void testBasicAuthConnection() {
    //TODO implement
    //GIVEN

    //WHEN

    //THEN
  }
}
