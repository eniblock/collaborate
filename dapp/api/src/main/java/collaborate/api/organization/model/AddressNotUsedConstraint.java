package collaborate.api.organization.model;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
      ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AddressNotUsedValidator.class)
public @interface AddressNotUsedConstraint {

  String message() default "The wallet address is already used by another organization.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}