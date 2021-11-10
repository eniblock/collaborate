package collaborate.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

  private final ObjectMapper objectMapper;

  /**
   * handle controller methods parameter validation exceptions
   *
   * @param exception ex
   * @return wrapped result
   */
  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<String> handle(ConstraintViolationException exception) {

    Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
    var body = violations.stream()
        .map(v -> v.getPropertyPath() + " " + v.getMessage())
        .collect(Collectors.joining("\n"));
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }
}
