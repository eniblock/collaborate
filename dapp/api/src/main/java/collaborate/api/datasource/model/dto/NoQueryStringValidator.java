package collaborate.api.datasource.model.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NoQueryStringValidator implements
    ConstraintValidator<NoQueryStringConstraint, String> {

  @Override
  public boolean isValid(String url,
      ConstraintValidatorContext constraintValidatorContext) {
    return url != null
        && !url.contains("?");
  }
}
