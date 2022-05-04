package collaborate.api.datasource.create;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.web.Attribute;
import collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatasourceDTOMetadataVisitorTest {

  ObjectMapper objectMapper = Mockito.spy(TestResources.objectMapper);

  @InjectMocks
  DatasourceDTOMetadataVisitor datasourceDTOMetadataVisitor;

  Metadata datasourceTypeMetadata = Metadata.builder()
      .name("datasource:type")
      .value("WebServerDatasource")
      .type("string")
      .build();
  Metadata datasourcePurposeMetadata = Metadata.builder()
      .name("datasource:purpose")
      .value("[\"digital-passport\",\"vehicles\"]")
      .type("string[]").build();


  @Test
  void buildResourceKeywords_withSimpleScope() {
    // GIVEN
    var keywords =
        Set.of(
            Attribute.builder()
                .name("provider:routing:alias")
                .value("aliasA").build(),
            Attribute.builder()
                .name("scope")
                .value("odometer").build(),
            Attribute.builder()
                .name("metadata:value.jsonPath")
                .value("$.odometer.mileage").build()
        );
    // WHEN
    var attributesResult = datasourceDTOMetadataVisitor.buildResourceKeywords(keywords);
    // THEN
    assertThat(attributesResult)
        .containsExactlyInAnyOrder(
            Metadata.builder()
                .name("aliasA:value.jsonPath")
                .value("$.odometer.mileage")
                .build()
        );
  }

  @Test
  void buildResourceKeywords_shouldReturnExpected_withSingleMetadata() {
    // GIVEN
    var keywords =
        Set.of(
            Attribute.builder()
                .name("provider:routing:alias")
                .value("aliasA").build(),
            Attribute.builder()
                .name("scope")
                .value("metric:odometer").build(),
            Attribute.builder()
                .name("metadata:value.jsonPath")
                .value("$._embedded.odometer.value")
                .type("Integer")
                .build()
        );

    // WHEN
    var attributesResult = datasourceDTOMetadataVisitor.buildResourceKeywords(keywords);
    // THEN
    assertThat(attributesResult)
        .containsExactlyInAnyOrder(
            Metadata.builder()
                .name("aliasA:value.jsonPath")
                .value("$._embedded.odometer.value")
                .type("Integer")
                .build()
        );
  }

  @Test
  void buildResourceKeywords_shouldReturnExpected_withMultipleMetadata() {
    // GIVEN
    var keywords =
        Set.of(
            Attribute.builder()
                .name("provider:routing:alias")
                .value("aliasA").build(),
            Attribute.builder()
                .name("scope")
                .value("assets:list-business-data").build(),
            Attribute.builder()
                .name("metadata:smart-contract-token")
                .value("true").build(),
            Attribute.builder()
                .name("metadata:assets-json-path")
                .value("$._embedded.business-data").build(),
            Attribute.builder()
                .name("metadata:asset-scope-json-path")
                .value("$.scope").build()
        );
    // WHEN
    var attributesResult = datasourceDTOMetadataVisitor.buildResourceKeywords(keywords);
    // THEN

    assertThat(attributesResult)
        .containsExactlyInAnyOrder(
            Metadata.builder()
                .name("aliasA:smart-contract-token")
                .value("true")
                .build(),
            Metadata.builder()
                .name("aliasA:assets-json-path")
                .value("$._embedded.business-data")
                .build(),
            Metadata.builder()
                .name("aliasA:asset-scope-json-path")
                .value("$.scope")
                .build()
        );
  }

  @Test
  void visitWebServerDatasource() throws DatasourceVisitorException {
    // GIVEN
    var datasource = CertificateBasedBasicAuthDatasourceFeatures.datasource;
    // WHEN
    var metadataResult = datasourceDTOMetadataVisitor.visitWebServerDatasource(datasource);
    // THEN
    assertThat(metadataResult).containsExactlyInAnyOrder(
        Metadata.builder()
            .name("datasource:purpose")
            .value("[\"vehicles\",\"digital-passport\"]")
            .type("string[]").build(),
        datasourceTypeMetadata
    );
  }

  @Test
  void buildType() {
    // GIVEN
    var expectedMetadata = datasourceTypeMetadata;
    // WHEN
    var metadataResult = datasourceDTOMetadataVisitor.buildType(
        CertificateBasedBasicAuthDatasourceFeatures.datasource);
    // THEN
    assertThat(metadataResult).isEqualTo(expectedMetadata);
  }
}
