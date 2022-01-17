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
        .keywords(Set.of("scope:metric:odometer"))
        .build();
  }

  @Test
  void validate_keywords_shouldNotReturnViolations_withScopeKeyword() {
    // GIVEN
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    assertThat(actualViolations).isEmpty();
  }

  @Test
  void validate_keywords_shouldNotReturnViolations_withPurposeKeyword() {
    // GIVEN
    webResource.setKeywords(Set.of("scope:list-asset"));
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
    webResource.setKeywords(Set.of("resource"));
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

  private static Stream<Arguments> findFirstKeywordRemovingPrefixParameters() {
    return Stream.of(
        Arguments.of("scope:", Optional.of("A")),
        Arguments.of("param:", Optional.of("")),
        Arguments.of("pre", Optional.empty())
    );
  }

  @ParameterizedTest
  @MethodSource("findFirstKeywordRemovingPrefixParameters")
  void findFirstKeywordRemovingPrefix(String prefix, Optional<String> expected) {
    // GIVEN
    Set<String> keywords = Set.of(
        "scope:A",
        "document:C",
        "param:"
    );
    var resource = WebServerResource.builder().keywords(keywords).build();
    // WHEN
    var keywordResult = resource.findFirstKeywordRemovingPrefix(prefix);
    // THEN
    assertThat(keywordResult.isPresent()).isEqualTo(expected.isPresent());
    expected.ifPresent(e ->
        assertThat(keywordResult).hasValue(e)
    );
  }
}
