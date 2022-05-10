package collaborate.api.datasource.model.dto.web;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.ATTR_NAME_TEST_CONNECTION;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HasAssetListValidator implements
    ConstraintValidator<HasAssetListConstraint, List<WebServerResource>> {

  @Override
  public boolean isValid(List<WebServerResource> resources,
      ConstraintValidatorContext constraintValidatorContext) {
    return resources != null
        && resources.stream()
        .anyMatch(resource -> Attribute.containsName(resource.getKeywords(), ATTR_NAME_TEST_CONNECTION));
  }
}
