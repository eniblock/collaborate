package collaborate.api.http;

import collaborate.api.datasource.RequestEntityVisitor;
import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.http.security.SSLContextFactory;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestEntityVisitorFactory {

  private final SSLContextFactory sslContextCreator;
  private final HttpClientFactory httpClientFactory;
  private final AccessTokenProvider accessTokenProvider;

  public RequestEntityVisitor create(URI uri) {
    return new RequestEntityVisitor(
        accessTokenProvider,
        httpClientFactory,
        new RequestEntityBuilder<>(uri.toString()),
        sslContextCreator
    );
  }

}
