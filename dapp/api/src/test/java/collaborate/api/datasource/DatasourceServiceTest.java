package collaborate.api.datasource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.create.CreateDatasourceDAO;
import collaborate.api.datasource.domain.Datasource;
import collaborate.api.datasource.domain.web.WebServerDatasource;
import collaborate.api.datasource.domain.web.authentication.Authentication;
import collaborate.api.datasource.domain.web.authentication.OAuth2;
import collaborate.api.datasource.repository.DataSourceRepository;
import collaborate.api.datasource.security.SaveAuthenticationToDatabaseVisitor;
import collaborate.api.datasource.traefik.TraefikService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class DatasourceServiceTest {

  @Mock
  private DataSourceRepository dataSourceRepository;
  @Mock
  private CreateDatasourceDAO createDatasourceDAO;
  @Mock
  private SaveAuthenticationToDatabaseVisitor saveAuthenticationToDatabaseVisitor;
  @Mock
  private EntityManager entityManager;
  @Mock
  private TraefikService traefikService;
  @InjectMocks
  private DatasourceService datasourceService;

  private final UUID datasourceUUID = UUID.fromString("50631325-f40d-4e45-be29-4dd44d54fc12");

  @Test
  void create_shouldCallExpectedServices_withValidOAuth() throws Exception {
    // GIVEN
    Authentication oauth = new OAuth2();
    Datasource dataSource = WebServerDatasource.builder()
        .id(datasourceUUID)
        .authMethod(oauth).build();

    when(dataSourceRepository.saveAndFlush(any(Datasource.class))).thenReturn(dataSource);
    doNothing().when(saveAuthenticationToDatabaseVisitor)
        .visitOAuth2((OAuth2) dataSource.getAuthMethod());
    doNothing().when(entityManager).refresh(dataSource);
    doNothing().when(traefikService).create(dataSource, datasourceUUID.toString());
    // WHEN
    datasourceService.create(dataSource, Optional.empty());
    // THEN
    verify(dataSourceRepository, times(1)).saveAndFlush(dataSource);
    verify(saveAuthenticationToDatabaseVisitor, times(1))
        .visitOAuth2((OAuth2) dataSource.getAuthMethod());
    verify(traefikService, times(1)).create(dataSource, datasourceUUID.toString());
  }

  @Test
  void search_shouldCallDatasourceRepository() {
    // GIVEN
    List<Datasource> list = new ArrayList<>();
    Pageable pageable = PageRequest.of(0, 20);
    String query = "";
    Page<Datasource> datasourcePage = new PageImpl<>(list, pageable, 0);
    when(dataSourceRepository.findByNameIgnoreCaseLike(pageable, query)).thenReturn(datasourcePage);

    // WHEN
    datasourceService.search(pageable, query);
    // THEN
    verify(dataSourceRepository, times(1))
        .findByNameIgnoreCaseLike(any(Pageable.class), anyString());
  }

  @Test
  void findById_shouldReturnExpectedDatasource_withExisingDatasource() {
    // GIVEN
    Datasource datasource = WebServerDatasource.builder()
        .id(datasourceUUID)
        .build();
    when(dataSourceRepository.findById(datasourceUUID)).thenReturn(Optional.of(datasource));
    // WHEN
    Optional<Datasource> actual = datasourceService.findById(datasourceUUID);
    // THEN
    assertThat(actual).isPresent();
    assertThat(actual.get().getId()).isEqualTo(datasourceUUID);
  }

  @Test
  void findById_shouldReturnNone_withUnexistingDatasource() {
    // GIVEN
    when(dataSourceRepository.findById(datasourceUUID)).thenReturn(Optional.empty());
    // WHEN
    var actualDatasource = datasourceService.findById(datasourceUUID);
    // THEN
    assertThat(actualDatasource).isNotPresent();
  }

}
