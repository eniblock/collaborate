package collaborate.api.datasource.model.dto.web;

import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.ROUTING_KEY_PREFIXES;
import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.TEST_CONNECTION;

import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ResourceKeywordValidator implements
    ConstraintValidator<ResourceKeywordConstraint, Collection<String>> {

  @Override
  public boolean isValid(Collection<String> strings,
      ConstraintValidatorContext constraintValidatorContext) {
    return strings != null
        && !strings.isEmpty()
        && strings
        .stream()
        .anyMatch(
            keyword ->
                ROUTING_KEY_PREFIXES.stream().anyMatch(keyword::startsWith)
                    || keyword.equals(TEST_CONNECTION)
        );
  }
}
