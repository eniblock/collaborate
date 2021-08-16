package collaborate.api.traefik.routing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class RoutingKeyKeywordValidatorTest {

  ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  Validator validator = factory.getValidator();

  @Test
  void isValidate_shouldBeFalse_withNullList() {
    // GIVEN
    var routingKeyValidator = new RoutingKeyKeywordValidator();
    // WHEN
    var actualIsValid = routingKeyValidator.isValid(null, null);
    // THEN
    assertThat(actualIsValid).isFalse();
  }

  @Test
  void isValidate_shouldBeTrue_withValidList() {
    // GIVEN
    var routingKeyValidator = new RoutingKeyKeywordValidator();
    var keywords = List.of("routing-key:route");
    // WHEN
    var actualIsValid = routingKeyValidator.isValid(keywords, null);
    // THEN
    assertThat(actualIsValid).isTrue();
  }
}
