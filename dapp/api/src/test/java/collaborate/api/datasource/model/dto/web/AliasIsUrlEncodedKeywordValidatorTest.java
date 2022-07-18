package collaborate.api.datasource.model.dto.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class AliasIsUrlEncodedKeywordValidatorTest {

  @Test
  void isValid_shouldBeTrue_withNullList() {
    // GIVEN
    var aliasIsUrlEncodedKeywordValidator = new AliasIsUrlEncodedKeywordValidator();
    // WHEN
    var actualIsValid = aliasIsUrlEncodedKeywordValidator.isValid(null, null);
    // THEN
    assertThat(actualIsValid).isTrue();
  }

  @Test
  void isValid_shouldBeTrue_withValidList() {
    // GIVEN
    var aliasIsUrlEncodedKeywordValidator = new AliasIsUrlEncodedKeywordValidator();
    var keywords = List.of(Attribute.builder()
        .name("provider:routing:alias")
        .value("route").build());
    // WHEN
    var actualIsValid = aliasIsUrlEncodedKeywordValidator.isValid(keywords, null);
    // THEN
    assertThat(actualIsValid).isTrue();
  }

  @Test
  void isValidate_shouldBeFalse_withInvalidCharacters() {
    // GIVEN
    var isHtmlAliasKeywordValidator = new AliasIsUrlEncodedKeywordValidator();
    var keywords = List.of(Attribute.builder()
        .name("provider:routing:alias")
        .value("route A").build());
    // WHEN

    var actualIsValid = isHtmlAliasKeywordValidator.isValid(keywords, null);
    // THEN
    assertThat(actualIsValid).isFalse();
  }
}
