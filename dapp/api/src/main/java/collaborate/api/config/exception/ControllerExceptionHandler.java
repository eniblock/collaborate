package collaborate.api.config.exception;

import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

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

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    String errorMessages = ex.getBindingResult().getFieldErrors().stream()
        .map(err -> new ErrorModel(err.getField(), err.getRejectedValue(), err.getDefaultMessage()))
        .distinct()
        .map(ErrorModel::getMessageError)
        .collect(Collectors.joining(" "));
    log.error("{}", errorMessages);
    return new ResponseEntity<>(
        new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMessages),
        HttpStatus.BAD_REQUEST
    );
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    return ResponseEntity.badRequest().body(new ErrorResponse(
        HttpStatus.BAD_REQUEST.getReasonPhrase(),
        ex.getMessage())
    );
  }

}
