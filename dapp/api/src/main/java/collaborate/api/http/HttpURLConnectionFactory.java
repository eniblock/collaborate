package collaborate.api.http;

import collaborate.api.datasource.domain.authentication.Authentication;
import collaborate.api.datasource.domain.authentication.BasicAuth;
import collaborate.api.datasource.domain.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.domain.authentication.Oauth;
import collaborate.api.http.security.SSLContextCreator;
import collaborate.api.http.security.SSLContextException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.UnrecoverableKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpURLConnectionFactory {

  private final SSLContextCreator sslContextCreator;

  public HttpURLConnection create(String url, Authentication authentication)
      throws IOException, SSLContextException, UnrecoverableKeyException {

    var httpURLConnectionBuilder = new HttpURLConnectionBuilder(url);
    if (authentication instanceof BasicAuth) {
      var basicAuth = ((BasicAuth) authentication);
      httpURLConnectionBuilder.authorizationBasic(basicAuth.getUser(), basicAuth.getPassword());
    }

    if (authentication instanceof CertificateBasedBasicAuth) {
      var certificateAuth = ((CertificateBasedBasicAuth) authentication);
      var sslContext = sslContextCreator.create(
          certificateAuth.getPfxFileContent(),
          certificateAuth.getPassphrase().toCharArray()
      );
      httpURLConnectionBuilder.sslContext(sslContext);
    }

    if (authentication instanceof Oauth){
      //TODO instantiate http connection
    }

    return httpURLConnectionBuilder.build();
  }

}
