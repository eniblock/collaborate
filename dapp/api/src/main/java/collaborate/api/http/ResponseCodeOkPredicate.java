package collaborate.api.http;

import static org.springframework.http.HttpStatus.OK;

import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ResponseCodeOkPredicate implements Predicate<Supplier<ResponseEntity<String>>> {

  @Override
  public boolean test(Supplier<ResponseEntity<String>> responseSupplier) {
    var response = responseSupplier.get();
    log.debug("testing connection  - responseCode={}", response.getStatusCode());
    return response.getStatusCode() == OK;
  }
}
