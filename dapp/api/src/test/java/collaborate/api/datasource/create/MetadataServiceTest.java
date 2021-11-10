package collaborate.api.datasource.create;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import collaborate.api.datasource.metadata.MetadataService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.test.TestResources;
import org.junit.jupiter.api.Test;

class MetadataServiceTest {

  MetadataService metadataService = new MetadataService(TestResources.objectMapper);

  Datasource datasource = TestResources.readContent(
      "/datasource/domain/web/datasource.json",
      Datasource.class
  );
  Datasource emptyDatasource = Datasource.builder()
      .providerMetadata(emptySet())
      .build();

  @Test
  void getDatasourceType_shouldReturnExpected_withExistingMetadata() {
    // GIVEN
    // WHEN
    var typeResult = metadataService.getType(datasource);
    // THEN
    assertThat(typeResult).isEqualTo("WebServerDatasourceDTO");
  }

  @Test
  void getDatasourceType_throwIllegalState_withMissingMetadata() {
    // GIVEN
    // THEN
    assertThrows(IllegalStateException.class, () -> {
      // WHEN
      metadataService.getType(emptyDatasource);
    });
  }

  @Test
  void getAuthentication_shouldReturnExpected_withExistingMetadata() {
    // GIVEN
    // WHEN
    var authenticationResult = metadataService.getAuthentication(datasource);
    // THEN
    assertThat(authenticationResult).isEqualTo("BasicAuth");
  }

  @Test
  void getAuthentication_shouldThrowIllegalState_withMissingMetadata() {
    // GIVEN
    // THEN
    assertThrows(IllegalStateException.class, () -> {
      // WHEN
      metadataService.getAuthentication(emptyDatasource);
    });
  }

  @Test
  void getCertificate_shouldReturnExpected_withExistingMetadata() {
    // GIVEN
    // WHEN
    var certificateResult = metadataService.getCertificate(datasource);
    // THEN
    assertThat(certificateResult).isEqualTo("caEmail");
  }

  @Test
  void getCertificate_shouldReturnEmpty_withMissingMetadata() {
    // GIVEN
    // WHEN
    var certificateResult = metadataService.getCertificate(emptyDatasource);
    // THEN
    assertThat(certificateResult).isEmpty();
  }

  @Test
  void getPurpose_shouldReturnExpected_withExistingMetadata() {
    // GIVEN
    // WHEN
    var purposeResult = metadataService.getPurpose(datasource);
    // THEN
    assertThat(purposeResult).containsExactlyInAnyOrder("digital-passport");
  }

  @Test
  void getPurpose_throwIllegalState_withMissingMetadata() {
    // GIVEN
    // THEN
    assertThrows(IllegalStateException.class, () -> {
      // WHEN
      metadataService.getPurpose(emptyDatasource);
    });
  }
}
