package collaborate.api.test.assertion;

import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import org.assertj.core.api.AbstractAssert;

public class ConstraintViolationSetAssert extends
    AbstractAssert<ConstraintViolationSetAssert, Set<? extends ConstraintViolation<?>>> {

  public ConstraintViolationSetAssert(Set<? extends ConstraintViolation<?>> actual) {
    super(actual, ConstraintViolationSetAssert.class);
  }

  public static ConstraintViolationSetAssert assertThat(
      Set<? extends ConstraintViolation<?>> actual) {
    return new ConstraintViolationSetAssert(actual);
  }

  public ConstraintViolationSetAssert hasViolationOnPath(String path) {
    isNotNull();
    if (!containsViolationWithPath(actual, path)) {
      failWithMessage(
          "Expected violation on path: " + path +
              "\nFound violation path:\n" +
              actual.stream().map(v -> v.getPropertyPath().toString())
                  .collect(Collectors.toSet()));
    }
    return this;
  }

  private boolean containsViolationWithPath(Set<? extends ConstraintViolation<?>> violations,
      String path) {
    return violations.stream()
        .anyMatch(violation -> violation.getPropertyPath().toString().equals(path));
  }

  public ConstraintViolationSetAssert hasNoViolations() {
    isNotNull();

    if (!actual.isEmpty()) {
      failWithMessage("Expecting no violations, but there are %s violations", actual.size());
    }
    return this;
  }

}