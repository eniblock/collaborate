package collaborate.api.http;

import static org.springframework.http.HttpStatus.OK;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HttpURLConnectionPredicate implements Predicate<HttpURLConnection> {

  @Override
  public boolean test(HttpURLConnection httpURLConnection) {
    try {
      httpURLConnection.connect();
      var result = httpURLConnection.getResponseCode() != OK.value();
      httpURLConnection.disconnect();
      return result;
    } catch (IOException e) {
      log.error("While testing connection to URL=" + httpURLConnection.getURL(), e);
      return false;
    }
  }
}
