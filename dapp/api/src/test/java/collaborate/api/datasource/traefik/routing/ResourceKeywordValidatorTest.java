package collaborate.api.datasource.traefik.routing;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.model.dto.web.ResourceKeywordValidator;
import java.util.List;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class ResourceKeywordValidatorTest {

  ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

  @Test
  void isValidate_shouldBeFalse_withNullList() {
    // GIVEN
    var routingKeyValidator = new ResourceKeywordValidator();
    // WHEN
    var actualIsValid = routingKeyValidator.isValid(null, null);
    // THEN
    assertThat(actualIsValid).isFalse();
  }

  @Test
  void isValidate_shouldBeTrue_withValidList() {
    // GIVEN
    var routingKeyValidator = new ResourceKeywordValidator();
    var keywords = List.of("scope:route");
    // WHEN
    var actualIsValid = routingKeyValidator.isValid(keywords, null);
    // THEN
    assertThat(actualIsValid).isTrue();
  }
}
