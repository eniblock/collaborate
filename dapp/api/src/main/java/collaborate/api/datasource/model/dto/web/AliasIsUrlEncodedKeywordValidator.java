package collaborate.api.datasource.model.dto.web;

import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.ATTR_NAME_ALIAS;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Ensure that the alias attribute is a valid html string
 */
public class AliasIsUrlEncodedKeywordValidator implements
    ConstraintValidator<AliasIsUrlEncodedKeywordConstraint, Collection<Attribute>> {

  @Override
  public boolean isValid(Collection<Attribute> attributes,
      ConstraintValidatorContext constraintValidatorContext) {
    if (attributes != null) {
      return attributes
          .stream()
          .filter(keyword ->
              StringUtils.equals(keyword.getName(), ATTR_NAME_ALIAS)
          ).findFirst()
          .map(Attribute::getValue)
          .map(this::isUrlEncoded)
          .orElse(true);
    }
    return true;
  }

  private boolean isUrlEncoded(String value) {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).equals(value);
    } catch (UnsupportedEncodingException e) {
      return false;
    }
  }
}
