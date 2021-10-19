package collaborate.api.http;

import collaborate.api.datasource.model.dto.web.authentication.Authentication;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.http.security.SSLContextException;
import collaborate.api.http.security.SSLContextFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.security.UnrecoverableKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpURLConnectionFactory {

  private final SSLContextFactory sslContextCreator;

  public HttpURLConnection create(URI uri, Authentication authentication)
      throws IOException, SSLContextException, UnrecoverableKeyException {

    var httpURLConnectionBuilder = new HttpURLConnectionBuilder(uri.toString());
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

    if (authentication instanceof OAuth2){
      //TODO v0.4.0
      throw new NotImplementedException();
    }

    return httpURLConnectionBuilder.build();
  }

}
