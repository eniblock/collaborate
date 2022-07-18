package collaborate.api.datasource.model.dto.web;

import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.ATTR_NAME_ALIAS;
import static collaborate.api.datasource.model.dto.web.WebServerResource.Keywords.ATTR_NAME_TEST_CONNECTION;

import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Ensure that at least one Attribute has the expected name
 */
public class HasAliasKeywordValidator implements
    ConstraintValidator<HasAliasKeywordConstraint, Collection<Attribute>> {

  @Override
  public boolean isValid(Collection<Attribute> attributes,
      ConstraintValidatorContext constraintValidatorContext) {
    return attributes != null
        && !attributes.isEmpty()
        && attributes
        .stream()
        .anyMatch(
            keyword ->
                StringUtils.equals(keyword.getName(), ATTR_NAME_ALIAS)
                    || keyword.getName().equals(ATTR_NAME_TEST_CONNECTION)
        );
  }
}
