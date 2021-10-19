package collaborate.api.datasource.model.dto.web;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.PURPOSE_TEST_CONNECTION;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HasTestConnectionValidator implements
    ConstraintValidator<HasTestConnectionConstraint, List<WebServerResource>> {

  @Override
  public boolean isValid(List<WebServerResource> resources,
      ConstraintValidatorContext constraintValidatorContext) {
    return resources != null
        && resources.stream()
        .anyMatch(resource -> resource.getKeywords() != null && resource.getKeywords()
            .contains(PURPOSE_TEST_CONNECTION));
  }
}