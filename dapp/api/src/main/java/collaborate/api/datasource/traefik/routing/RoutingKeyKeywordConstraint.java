package collaborate.api.datasource.traefik.routing;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = RoutingKeyKeywordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RoutingKeyKeywordConstraint {

  String message() default "Missing keyword with prefix '"
      + RoutingKeyKeywordValidator.KEYWORD_PREFIX + "'";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
