package collaborate.api.datasource.model.dto;

import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DatasourcePurposeValidator implements
    ConstraintValidator<DatasourcePurposeConstraint, Set<String>> {

  @Override
  public boolean isValid(Set<String> keywords,
      ConstraintValidatorContext constraintValidatorContext) {
    return keywords != null
        && keywords.stream()
        .anyMatch(keyword -> DatasourcePurpose.getKeywords().contains(keyword));
  }
}