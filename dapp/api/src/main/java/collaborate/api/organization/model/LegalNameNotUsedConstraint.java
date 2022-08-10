package collaborate.api.organization.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
      ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LegalNameNotUsedValidator.class)
public @interface LegalNameNotUsedConstraint {

  String message() default "The organization name is already used by another organization.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}