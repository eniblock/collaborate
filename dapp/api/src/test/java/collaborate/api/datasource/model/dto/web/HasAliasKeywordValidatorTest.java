package collaborate.api.datasource.model.dto.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class HasAliasKeywordValidatorTest {

  @Test
  void isValid_shouldBeFalse_withNullList() {
    // GIVEN
    var routingKeyValidator = new HasAliasKeywordValidator();
    // WHEN
    var actualIsValid = routingKeyValidator.isValid(null, null);
    // THEN
    assertThat(actualIsValid).isFalse();
  }

  @Test
  void isValid_shouldBeTrue_withValidList() {
    // GIVEN
    var routingKeyValidator = new HasAliasKeywordValidator();
    var keywords = List.of(Attribute.builder()
        .name("provider:routing:alias")
        .value("route").build());
    // WHEN
    var actualIsValid = routingKeyValidator.isValid(keywords, null);
    // THEN
    assertThat(actualIsValid).isTrue();
  }
}
