package collaborate.api.datasource.domain;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = NoQueryStringValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoQueryStringConstraint {

  String message() default "URL should not has query string suffix";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
