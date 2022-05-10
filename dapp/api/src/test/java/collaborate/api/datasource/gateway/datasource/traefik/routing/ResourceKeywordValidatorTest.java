package collaborate.api.datasource.gateway.datasource.traefik.routing;

import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.datasource.model.dto.web.Attribute;
import collaborate.api.datasource.model.dto.web.ResourceKeywordValidator;
import java.util.List;
import org.junit.jupiter.api.Test;

class ResourceKeywordValidatorTest {

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
    var keywords = List.of(Attribute.builder()
            .name("provider:routing:alias")
            .value("route").build());
    // WHEN
    var actualIsValid = routingKeyValidator.isValid(keywords, null);
    // THEN
    assertThat(actualIsValid).isTrue();
  }
}
