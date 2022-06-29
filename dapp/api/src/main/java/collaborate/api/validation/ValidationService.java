package collaborate.api.validation;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ValidationService {

  public void validateOrThrowResponseStatus(Object objectToValid, HttpStatus httpStatus) {
    try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
      var violations = validatorFactory.getValidator().validate(objectToValid);
      if (!CollectionUtils.isEmpty(violations)) {
        throw new ResponseStatusException(httpStatus,
            new ConstraintViolationException(violations).getMessage());
      }
    }
  }
}
