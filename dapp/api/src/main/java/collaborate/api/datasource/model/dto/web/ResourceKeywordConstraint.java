package collaborate.api.datasource.model.dto.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = ResourceKeywordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceKeywordConstraint {

  String message() default "Resource keyword should contain a keyword prefixed with 'scope:' or 'purpose:' prefix";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}