package collaborate.api.traefik.routing;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RoutingKeyKeywordValidator implements
    ConstraintValidator<RoutingKeyKeywordConstraint, List<String>> {

  public static final String KEYWORD_PREFIX = "routing-key:";

  @Override
  public boolean isValid(List<String> strings,
      ConstraintValidatorContext constraintValidatorContext) {
    return strings != null
        && !strings.isEmpty()
        && strings.stream()
        .filter(k -> k.startsWith(KEYWORD_PREFIX))
        .count() == 1;
  }
}