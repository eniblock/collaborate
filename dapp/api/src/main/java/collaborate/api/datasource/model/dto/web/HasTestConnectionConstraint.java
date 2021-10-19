package collaborate.api.datasource.model.dto.web;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = HasTestConnectionValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HasTestConnectionConstraint {

  String message() default "Datasource should have at least on resource with 'purpose:test-connection' keyword";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
