package collaborate.api.datasource.kpi.find;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = KpiQueryDateFormatValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface KpiQueryDateFormatConstraint {

  String message() default "Datasource should have one of [business-data, digital-passport] keyword";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
