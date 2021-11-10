package collaborate.api.datasource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.metadata.MetadataService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.enumeration.DatasourceStatus;
import collaborate.api.test.TestResources;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListDatasourceDTOFactoryTest {

  @Mock
  MetadataService metadataService;

  @InjectMocks
  ListDatasourceDTOFactory listDatasourceDTOFactory;

  Datasource datasource = TestResources.readPath(
      "/datasource/domain/web/datasource.json",
      Datasource.class
  );

  @Test
  void create_shouldInitCreationDate() {
    // GIVEN
    // WHEN
    var detailsResult = listDatasourceDTOFactory.create(datasource);
    // THEN
    assertThat(detailsResult.getCreationDateTime()).isEqualTo("2021-10-25T13:42:01.343Z");
  }

  @Test
  void create_shouldInitDatasourceType() {
    // GIVEN
    when(metadataService.getType(datasource)).thenReturn("WebServerDatasourceDTO");
    // WHEN
    var detailsResult = listDatasourceDTOFactory.create(datasource);
    // THEN
    assertThat(detailsResult.getDatasourceType()).isEqualTo("WebServerDatasourceDTO");
  }

  @Test
  void create_shouldInitId() {
    // GIVEN
    // WHEN
    var detailsResult = listDatasourceDTOFactory.create(datasource);
    // THEN
    assertThat(detailsResult.getId()).isEqualTo("898df5a9-0970-4908-8226-ecdfd7161060");
  }

  @Test
  void create_shouldInitName() {
    // GIVEN
    // WHEN
    var detailsResult = listDatasourceDTOFactory.create(datasource);
    // THEN
    assertThat(detailsResult.getName()).isEqualTo("DSPConsortium1 Digital Passport");
  }

  @Test
  void create_shouldInitPurpose() {
    // GIVEN
    when(metadataService.getPurpose(datasource)).thenReturn(List.of("digital-passport"));
    // WHEN
    var detailsResult = listDatasourceDTOFactory.create(datasource);
    // THEN
    assertThat(detailsResult.getPurpose()).containsExactlyInAnyOrder("digital-passport");
  }

  @Test
  void create_shouldNotInitNbGrantedAccess() {
    // GIVEN
    // WHEN
    var detailsResult = listDatasourceDTOFactory.create(datasource);
    // THEN
    // THis test would be update on nbGrantedAccess implementation
    assertThat(detailsResult.getNbGrantedAccess()).isNull();
  }

  @Test
  void create_shouldInitStatus() {
    // GIVEN
    // WHEN
    var detailsResult = listDatasourceDTOFactory.create(datasource);
    // THEN
    assertThat(detailsResult.getStatus()).isEqualTo(DatasourceStatus.CREATED);
  }

}
