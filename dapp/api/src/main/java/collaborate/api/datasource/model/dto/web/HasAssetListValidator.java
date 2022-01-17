package collaborate.api.datasource.model.dto.web;

import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.TEST_CONNECTION;

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
        .anyMatch(resource -> resource.getKeywords() != null && resource.getKeywords()
            .contains(TEST_CONNECTION));
  }
}
