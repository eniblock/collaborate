package collaborate.api.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User id is not found")
public class UserIdNotFoundException extends Exception {

    public UserIdNotFoundException(UUID userId) {
        super("User id not found: " + userId);
    }

}
