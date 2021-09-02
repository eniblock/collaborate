package collaborate.api.datasource.traefik.routing;

import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RoutingKeyKeywordValidator implements
    ConstraintValidator<RoutingKeyKeywordConstraint, Collection<String>> {

  public static final String KEYWORD_PREFIX = "routing-key:";

  @Override
  public boolean isValid(Collection<String> strings,
      ConstraintValidatorContext constraintValidatorContext) {
    return strings != null
        && !strings.isEmpty()
        && strings.stream()
        .filter(k -> k.startsWith(KEYWORD_PREFIX))
        .count() == 1;
  }
}