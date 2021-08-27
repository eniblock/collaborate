package collaborate.api.datasource.domain.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebServerResourceTest {

  ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  Validator validator = factory.getValidator();
  WebServerResource webResource;

  @BeforeEach
  void setup() {
    webResource = WebServerResource.builder()
        .url("/test")
        .keywords(Set.of("routing-key:resource"))
        .build();
  }

  @Test
  void validationOnKeywords_shouldNotReturnViolations_withRoutingKeyKeyword() {
    // GIVEN
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    assertThat(actualViolations).isEmpty();
  }

  @Test
  void validationOnKeywords_shouldReturnViolations_withNullKeywords() {
    // GIVEN
    webResource.setKeywords(null);
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    assertThat(actualViolations).hasSize(1);
    assertThat(actualViolations.iterator().next().getPropertyPath()).hasToString("keywords");
  }

  @Test
  void validationOnKeywords_shouldReturnViolations_withMissingRoutingKeyKeywords() {
    // GIVEN
    webResource.setKeywords(Set.of("resource"));
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    assertThat(actualViolations).hasSize(1);
    assertThat(actualViolations.iterator().next().getPropertyPath()).hasToString("keywords");
  }

  @Test
  void validationOnUrl_shouldReturnViolation_withNullUrl() {
    // GIVEN
    webResource.setUrl(null);
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    assertThat(actualViolations).hasSize(1);
    assertThat(actualViolations.iterator().next().getPropertyPath()).hasToString("url");
  }

  @Test
  void validationOnUrl_shouldReturnViolation_withQueryString() {
    // GIVEN
    webResource.setUrl("test?p=v");
    // WHEN
    var actualViolations = validator.validate(webResource);
    // THEN
    assertThat(actualViolations).hasSize(1);
    assertThat(actualViolations.iterator().next().getPropertyPath()).hasToString("url");
  }
}
