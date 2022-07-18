package collaborate.api.datasource.model.dto.web;

import static collaborate.api.datasource.gateway.traefik.routing.RoutingKeyFromKeywordSupplier.ATTR_NAME_ALIAS;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = AliasIsUrlEncodedKeywordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AliasIsUrlEncodedKeywordConstraint {

  String message() default "The resource keyword '" + ATTR_NAME_ALIAS
      + " should be a valid URL encoded string"
      + "'";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
