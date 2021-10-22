package collaborate.api.http;

import collaborate.api.datasource.HttpURLConnectionVisitor;
import collaborate.api.http.security.SSLContextFactory;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpURLConnectionVisitorFactory {

  private final SSLContextFactory sslContextCreator;
  private final HttpClientFactory httpClientFactory;

  public HttpURLConnectionVisitor create(URI uri) {
    return new HttpURLConnectionVisitor(
        new HttpURLConnectionBuilder(uri.toString()),
        httpClientFactory,
        sslContextCreator
    );
  }

}
