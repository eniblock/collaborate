package collaborate.api.datasource.create;

import static collaborate.api.datasource.create.ProviderMetadataFactory.METADATA_REGEXP;
import static collaborate.api.datasource.create.ProviderMetadataFactory.NAME_GROUP_INDEX;
import static collaborate.api.datasource.create.ProviderMetadataFactory.TYPE_GROUP_INDEX;
import static collaborate.api.datasource.create.ProviderMetadataFactory.VALUE_GROUP_INDEX;
import static collaborate.api.test.TestResources.readPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import collaborate.api.datasource.model.Attribute;
import collaborate.api.datasource.model.dto.web.CertificateBasedBasicAuthDatasourceFeatures;
import collaborate.api.datasource.model.dto.web.WebServerDatasourceDTO;
import collaborate.api.datasource.model.dto.web.WebServerResource;
import collaborate.api.test.TestResources;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProviderMetadataFactoryTest {

  @Mock
  AuthenticationProviderMetadataVisitor authenticationProviderMetadataVisitor;

  @Spy
  ObjectMapper objectMapper = Mockito.spy(TestResources.objectMapper);

  @InjectMocks
  ProviderMetadataFactory providerMetadataFactory;

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
  void fromWebServerDatasource_withSimpleScope() {
    // GIVEN
    Set<String> keywords =
        Set.of(
            "scope:odometer",
            "metadata:value.jsonPath:$.odometer.mileage");
    WebServerDatasourceDTO webServerDatasourceDTO = CertificateBasedBasicAuthDatasourceFeatures.datasource;
    webServerDatasourceDTO.setResources(
        List.of(WebServerResource.builder().keywords(keywords).build()));

    // WHEN
    var attributesResult = providerMetadataFactory.from(webServerDatasourceDTO);
    // THEN
    assertThat(attributesResult)
        .containsAnyOf(
            Attribute.builder()
                .name("datasource:purpose")
                .value("[\"digital-passport\",\"vehicles\"]")
                .type("string[]").build(),
            Attribute.builder()
                .name("scope:odometer:value.jsonPath")
                .value("$.odometer.mileage")
                .build());
  }

  @Test
  void fromWebServerDatasource_shouldReturnExpected_withSingleMetadata() {
    // GIVEN
    Set<String> keywords =
        Set.of(
            "scope:metric:odometer",
            "metadata:value.jsonPath:$._embedded.odometer.value:Integer");
    WebServerDatasourceDTO webServerDatasourceDTO = CertificateBasedBasicAuthDatasourceFeatures.datasource;
    webServerDatasourceDTO.setResources(
        List.of(WebServerResource.builder().keywords(keywords).build()));

    // WHEN
    var attributesResult = providerMetadataFactory.from(webServerDatasourceDTO);
    // THEN
    assertThat(attributesResult)
        .containsAnyOf(
            Attribute.builder()
                .name("datasource:purpose")
                .value("[\"digital-passport\",\"vehicles\"]")
                .type("string[]").build(),
            Attribute.builder()
                .name("scope:metric:odometer:value.jsonPath")
                .value("$._embedded.odometer.value")
                .type("Integer")
                .build());
  }

  @Test
  void fromWebServerDatasource_shouldReturnExpected_withMultipleMetadata() {
    // GIVEN
    Set<String> keywords =
        Set.of(
            "scope:assets:list-business-data",
            "metadata:smart-contract-token:true",
            "metadata:assets-json-path:$._embedded.business-data",
            "metadata:asset-scope-json-path:$.scope");
    WebServerDatasourceDTO webServerDatasourceDTO =
        readPath(
            "/datasource/domain/web/certificateBasedBasicAuthDatasource.json",
            WebServerDatasourceDTO.class);
    webServerDatasourceDTO.setResources(
        List.of(WebServerResource.builder().keywords(keywords).build()));
    // WHEN
    var attributesResult = providerMetadataFactory.from(webServerDatasourceDTO);
    // THEN
    assertThat(attributesResult)
        .containsAnyOf(
            Attribute.builder()
                .name("datasource:purpose")
                .value("[\"digital-passport\",\"vehicles\"]")
                .type("string[]").build(),
            Attribute.builder()
                .name("scope:assets:list-business-data:smart-contract-token")
                .value("true")
                .build(),
            Attribute.builder()
                .name("scope:assets:list-business-data:assets-json-path")
                .value("$._embedded.business-data")
                .build(),
            Attribute.builder()
                .name("scope:assets:list-business-data:asset-scope-json-path")
                .value("$.scope")
                .build());
  }

  @Test
  void fromWebServerDatasource_shouldGenerateBasicAuthKey_withBasicAuthDto() {
    // GIVEN
    var datasourceDto = CertificateBasedBasicAuthDatasourceFeatures.datasource;
    when(authenticationProviderMetadataVisitor.visitCertificateBasedBasicAuth(any()))
        .thenCallRealMethod();
    when(authenticationProviderMetadataVisitor.visitBasicAuth(any())).thenCallRealMethod();
    // WHEN
    var metadataResult = providerMetadataFactory.from(datasourceDto);
    // THEN

    assertThat(metadataResult)
        .contains(
            Attribute.builder()
                .name("configuration.required.basicAuth")
                .type("Boolean")
                .value("true")
                .build());
  }
}
