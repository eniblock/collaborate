package collaborate.api.datasource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.domain.BasicAuthDto;
import collaborate.api.datasource.domain.DataSource;
import collaborate.api.datasource.domain.DatasourceClientSecret;
import collaborate.api.datasource.domain.authentication.Authentication;
import collaborate.api.datasource.domain.authentication.BasicAuth;
import collaborate.api.datasource.domain.authentication.Oauth;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.repository.DataSourceRepository;
import collaborate.api.security.VaultService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class DatasourceServiceTest {

  @Mock
  private VaultService vaultService;

  @Mock
  private DataSourceRepository dataSourceRepository;

  @Mock
  private DatasourceTestConnectionFactory datasourceTestConnectionFactory;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private DatasourceService datasourceService;

  @Test
  void create_ok() {
    // fixme test broken
//    //GIVEN
//    Authentication oauth = new Oauth();
//    DataSource dataSource = WebServerDatasource.builder().authMethod(oauth).build();
//    when(dataSourceRepository.save(any(DataSource.class))).thenReturn(dataSource);
//    doNothing().when(vaultService).saveClientSecret(anyString(), any(DatasourceClientSecret.class));
//    //WHEN
//    datasourceService.create(dataSource);
//    //THEN
//    verify(dataSourceRepository, times(1)).save(any(DataSource.class));
//    verify(vaultService, times(1)).saveClientSecret(anyString(), any(DatasourceClientSecret.class));
  }

  @Test
  void search_ok() {
    //GIVEN
    List<DataSource> list = new ArrayList<>();
    Pageable pageable = PageRequest.of(0, 20);
    String query = "";
    Page<DataSource> datasourcePage = new PageImpl<>(list, pageable, list.size());
    when(dataSourceRepository
        .findByNameIgnoreCaseLike(pageable, query)).thenReturn(datasourcePage);

    //WHEN
    Page<DataSource> actual = datasourceService.search(pageable, query);
    //THEN
    verify(dataSourceRepository, times(1))
        .findByNameIgnoreCaseLike(any(Pageable.class), anyString());
  }

  @Test
  void findById() {
    //GIVEN
    DataSource datasource = WebServerDatasource.builder().id(1L).build();
    when(dataSourceRepository.findById(anyLong())).thenReturn(Optional.of(datasource));
    //WHEN
    DataSource actual = datasourceService.findById(1L);
    //THEN
    assertThat(actual.getId()).isEqualTo(datasource.getId());
  }

  @Test
  void findById_should_throw_NotFound_Exception() {
    //GIVEN
    when(dataSourceRepository.findById(anyLong())).thenReturn(Optional.empty());
    //WHEN //THEN
    Assertions.assertThatThrownBy(() -> datasourceService.findById(1L)).isInstanceOf(
        ResponseStatusException.class);

  }

  @Test
  void saveBasicAuthCredentials_should_trigger_vaultService() {
    //GIVEN
    BasicAuth basicAuth = new BasicAuth();
    BasicAuthDto dto = new BasicAuthDto();
    dto.setUser("user");
    dto.setPassword("password");
    DataSource datasource = WebServerDatasource.builder().id(1L).authMethod(basicAuth).build();
    when(modelMapper.map(any(), any())).thenReturn(dto);
    //WHEN
    datasourceService.saveBasicAuthCredentials(datasource);
    //THEN
    verify(vaultService, times(1)).saveSecret(anyString(), any(BasicAuthDto.class));
  }

  @Test
  void saveOAuthCredentials_should_trigger_vaultService() {
    //GIVEN
    Oauth oauth = new Oauth();
    oauth.setClientId("clientId");
    oauth.setClientSecret("clientSecret");
    DataSource datasource = WebServerDatasource.builder().id(1L).authMethod(oauth).build();
    //WHEN
    datasourceService.saveOAuthCredentials(datasource);
    //THEN
    verify(vaultService, times(1)).saveClientSecret(anyString(), any(DatasourceClientSecret.class));
  }

  @Test
  void testBasicAuthConnection() {
    // TODO implement
    //GIVEN

    //WHEN

    //THEN
  }
}
