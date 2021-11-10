package collaborate.api.http;

import collaborate.api.datasource.OAuth2JWTProvider;
import collaborate.api.datasource.RequestEntityVisitor;
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
  private final OAuth2JWTProvider oAuth2JWTProvider;

  public RequestEntityVisitor create(URI uri) {
    return new RequestEntityVisitor(
        oAuth2JWTProvider,
        httpClientFactory,
        new RequestEntityBuilder<>(uri.toString()),
        sslContextCreator
    );
  }

}
