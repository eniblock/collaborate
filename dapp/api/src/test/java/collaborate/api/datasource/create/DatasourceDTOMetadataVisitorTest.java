package collaborate.api.datasource.create;

import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Regexp.METADATA_REGEXP;
import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Regexp.NAME_GROUP_INDEX;
import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Regexp.TYPE_GROUP_INDEX;
import static collaborate.api.datasource.create.DatasourceDTOMetadataVisitor.Regexp.VALUE_GROUP_INDEX;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.model.Metadata;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.regex.Matcher;
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
  void METADATA_REGEXP_shouldMatch_withKeyHavingDotChars() {
    // GIVEN
    String input = "metadata:response.jsonPath:$._embedded.odometer.value:Integer";
    Matcher matcher = METADATA_REGEXP.matcher(input);
    // WHEN
    var found = matcher.matches();
    // THEN
    assertThat(found).isTrue();
    assertThat(matcher.group(NAME_GROUP_INDEX)).isEqualTo("response.jsonPath");
    assertThat(matcher.group(VALUE_GROUP_INDEX)).isEqualTo("$._embedded.odometer.value");
    assertThat(matcher.group(TYPE_GROUP_INDEX)).isEqualTo("Integer");
  }

  @Test
  void METADATA_REGEXP_shouldMatch_withKeyWithoutSpecialChars() {
    // GIVEN
    String input = "metadata:response:$._embedded.odometer.value:Integer";
    Matcher matcher = METADATA_REGEXP.matcher(input);
    // WHEN
    var found = matcher.matches();
    // THEN
    assertThat(found).isTrue();
    assertThat(matcher.group(NAME_GROUP_INDEX)).isEqualTo("response");
    assertThat(matcher.group(VALUE_GROUP_INDEX)).isEqualTo("$._embedded.odometer.value");
    assertThat(matcher.group(TYPE_GROUP_INDEX)).isEqualTo("Integer");
  }

  @Test
  void METADATA_REGEXP_shouldMatch_withoutTypeGroup() {
    // GIVEN
    String input = "metadata:value.jsonPath:$._embedded.odometer.value";
    Matcher matcher = METADATA_REGEXP.matcher(input);

    // WHEN
    var found = matcher.matches();
    // THEN
    assertThat(found).isTrue();
    assertThat(matcher.group(NAME_GROUP_INDEX)).isEqualTo("value.jsonPath");
    assertThat(matcher.group(VALUE_GROUP_INDEX)).isEqualTo("$._embedded.odometer.value");
    assertThat(matcher.group(TYPE_GROUP_INDEX)).isNull();
  }

  @Test
  void METADATA_REGEXP_shouldNotMatch_withKeywordNotStartingWithMetadataPrefix() {
    // GIVEN
    String input = "value.jsonPath:$._embedded.odometer.value";
    Matcher matcher = METADATA_REGEXP.matcher(input);
    // WHEN
    var found = matcher.matches();
    // THEN
    assertThat(found).isFalse();
  }

  @Test
  void buildResourceKeywords_withSimpleScope() {
    // GIVEN
    Set<String> keywords =
        Set.of(
            "scope:odometer",
            "metadata:value.jsonPath:$.odometer.mileage");
    // WHEN
    var attributesResult = datasourceDTOMetadataVisitor.buildResourceKeywords(keywords);
    // THEN
    assertThat(attributesResult)
        .containsExactlyInAnyOrder(
            Metadata.builder()
                .name("scope:odometer:value.jsonPath")
                .value("$.odometer.mileage")
                .build()
        );
  }

  @Test
  void buildResourceKeywords_shouldReturnExpected_withSingleMetadata() {
    // GIVEN
    Set<String> keywords =
        Set.of(
            "scope:metric:odometer",
            "metadata:value.jsonPath:$._embedded.odometer.value:Integer");

    // WHEN
    var attributesResult = datasourceDTOMetadataVisitor.buildResourceKeywords(keywords);
    // THEN
    assertThat(attributesResult)
        .containsExactlyInAnyOrder(
            Metadata.builder()
                .name("scope:metric:odometer:value.jsonPath")
                .value("$._embedded.odometer.value")
                .type("Integer")
                .build()
        );
  }

  @Test
  void buildResourceKeywords_shouldReturnExpected_withMultipleMetadata() {
    // GIVEN
    Set<String> keywords =
        Set.of(
            "scope:assets:list-business-data",
            "metadata:smart-contract-token:true",
            "metadata:assets-json-path:$._embedded.business-data",
            "metadata:asset-scope-json-path:$.scope");
    // WHEN
    var attributesResult = datasourceDTOMetadataVisitor.buildResourceKeywords(keywords);
    // THEN

    assertThat(attributesResult)
        .containsExactlyInAnyOrder(
            Metadata.builder()
                .name("scope:assets:list-business-data:smart-contract-token")
                .value("true")
                .build(),
            Metadata.builder()
                .name("scope:assets:list-business-data:assets-json-path")
                .value("$._embedded.business-data")
                .build(),
            Metadata.builder()
                .name("scope:assets:list-business-data:asset-scope-json-path")
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
