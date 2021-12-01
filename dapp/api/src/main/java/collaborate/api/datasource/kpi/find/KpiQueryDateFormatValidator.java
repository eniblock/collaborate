package collaborate.api.datasource.kpi.find;

import collaborate.api.datasource.model.dto.DatasourcePurpose;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class KpiQueryDateFormatValidator implements
    ConstraintValidator<KpiQueryDateFormatConstraint, Set<String>> {

  @Override
  public boolean isValid(Set<String> keywords,
      ConstraintValidatorContext constraintValidatorContext) {
    return keywords != null
        && keywords.stream()
        .anyMatch(keyword -> DatasourcePurpose.getKeywords().contains(keyword));
  }
}
