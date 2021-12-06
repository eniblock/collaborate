package collaborate.api.datasource.model.dto.web;

import static collaborate.api.test.TestResources.readContent;
import static org.assertj.core.api.Assertions.assertThat;

import collaborate.api.test.assertion.ConstraintViolationSetAssert;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WebServerDatasourceDTOTest {

  ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
  Validator validator = factory.getValidator();
  WebServerDatasourceDTO webServerDatasource;

  @BeforeEach
  void setup() {
    webServerDatasource = readContent(
        "/datasource/model/web/certificateBasedBasicAuthDatasource.json",
        WebServerDatasourceDTO.class);
  }

  @Test
  void validation_shouldNotReturnViolations_withBusinessDataKeyword() {
    // GIVEN
    webServerDatasource.setKeywords(new HashSet<>(Set.of("business-data")));
    // WHEN
    var actualViolations = validator.validate(webServerDatasource);
    // THEN
    assertThat(actualViolations).isEmpty();
  }

  @Test
  void validation_shouldNotReturnViolations_withDigitalPassportKeyword() {
    // GIVEN
    webServerDatasource.setKeywords(new HashSet<>(Set.of("digital-passport")));
    // WHEN
    var actualViolations = validator.validate(webServerDatasource);
    // THEN
    assertThat(actualViolations).isEmpty();
  }

  @Test
  void validation_shouldReturnViolations_withNullKeywords() {
    // GIVEN
    webServerDatasource.setKeywords(null);
    // WHEN
    var violationsResult = validator.validate(webServerDatasource);
    // THEN
    ConstraintViolationSetAssert.assertThat(violationsResult).hasViolationOnPath("keywords");
  }

  @Test
  void validation_shouldReturnViolations_withNoResourceHavingPurposeTestConnectionKeyword() {
    // GIVEN
    webServerDatasource.setResources(List.of(new WebServerResource()));
    // WHEN
    var violationsResult = validator.validate(webServerDatasource);
    // THEN
    ConstraintViolationSetAssert.assertThat(violationsResult).hasViolationOnPath("resources");
  }

}
