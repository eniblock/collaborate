package collaborate.api.datasource.model.dto.web;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.assertion.ConstraintViolationSetAssert;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WebServerResourceTest {

  ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  Validator validator = factory.getValidator();
  WebServerResource webResource;

  @BeforeEach
  void setup() {
    webResource = WebServerResource.builder()
        .url("/test")
        .keywords(Set.of(
            Attribute.builder()
                .name("provider:routing:alias")
                .value("metric-odometer")
                .build(),
            Attribute.builder()
                .name("scope")
                .value("metric:odometer")
                .build()
        )).build();
  }

  @Test
  void validate_keywords_shouldNotReturnViolations_withAliasKeyword() {
    // GIVEN
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    assertThat(actualViolations).isEmpty();
  }

  @Test
  void validate_keywords_shouldNotReturnViolations_withPurposeKeyword() {
    // GIVEN
    webResource.setKeywords(Set.of(Attribute.builder()
        .name("list-asset")
        .build()));
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    assertThat(actualViolations).isEmpty();
  }

  @Test
  void validate_keywords_shouldReturnViolations_withNullKeywords() {
    // GIVEN
    webResource.setKeywords(null);
    // WHEN
    var violationsResult = validator.validate(webResource);
    // THEN
    ConstraintViolationSetAssert.assertThat(violationsResult).hasViolationOnPath("keywords");
  }

  @Test
  void validate_keywords_shouldReturnViolations_withMissingScopeKeywords() {
    // GIVEN
    webResource.setKeywords(Set.of(Attribute.builder()
        .name("resource")
        .build()));
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    ConstraintViolationSetAssert.assertThat(actualViolations).hasViolationOnPath("keywords");
  }

  @Test
  void validate_url_shouldReturnViolation_withNullUrl() {
    // GIVEN
    webResource.setUrl(null);
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    ConstraintViolationSetAssert.assertThat(actualViolations).hasViolationOnPath("url");
  }

  @Test
  void validation_url_shouldReturnViolation_withQueryString() {
    // GIVEN
    webResource.setUrl("test?p=v");
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    assertThat(actualViolations).hasSize(1);
    assertThat(actualViolations.iterator().next().getPropertyPath()).hasToString("url");
  }

  private static Stream<Arguments> findFirstKeywordByNameParameters() {
    return Stream.of(
        Arguments.of("scope", Optional.of("A")),
        Arguments.of("param", Optional.of("")),
        Arguments.of("pre", Optional.empty())
    );
  }

  @ParameterizedTest
  @MethodSource("findFirstKeywordByNameParameters")
  void findFirstKeywordByName(String prefix, Optional<String> expected) {
    // GIVEN
    var keywords = Set.of(
        Attribute.builder()
            .name("scope")
            .value("A")
            .build(),
        Attribute.builder()
            .name("document")
            .value("C")
            .build(),
        Attribute.builder()
            .name("param")
            .value("")
            .build()
    );
    var resource = WebServerResource.builder().keywords(keywords).build();
    // WHEN
    var keywordResult = resource.findFirstKeywordValueByName(prefix);
    // THEN
    assertThat(keywordResult.isPresent()).isEqualTo(expected.isPresent());
    expected.ifPresent(e ->
        assertThat(keywordResult).hasValue(e)
    );
  }
}
