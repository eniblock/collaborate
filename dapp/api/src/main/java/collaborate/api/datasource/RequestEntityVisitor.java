package collaborate.api.datasource;

import static java.util.Objects.requireNonNull;

import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.http.HttpClientFactory;
import collaborate.api.http.RequestEntityBuilder;
import collaborate.api.http.security.SSLContextFactory;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.function.Supplier;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public class RequestEntityVisitor implements
    AuthenticationVisitor<Supplier<ResponseEntity<JsonNode>>> {

  private final OAuth2JWTProvider oAuth2JWTProvider;
  private final HttpClientFactory httpClientFactory;
  private final RequestEntityBuilder<?> requestEntityBuilder;
  private final SSLContextFactory sslContextCreator;

  @Override
  public Supplier<ResponseEntity<JsonNode>> visitBasicAuth(BasicAuth basicAuth) {
    var restTemplate = new RestTemplate();
    addBasicAuth(basicAuth);
    return () -> restTemplate.exchange(requestEntityBuilder.build(), JsonNode.class);
  }

  private void addBasicAuth(BasicAuth basicAuth) {
    requestEntityBuilder.authorizationBasic(basicAuth.getUser(), basicAuth.getPassword());
  }

  @Override
  public Supplier<ResponseEntity<JsonNode>> visitCertificateBasedBasicAuth(
      CertificateBasedBasicAuth certificateBasedBasicAuth) {
    addBasicAuth(certificateBasedBasicAuth);

    try {
      var restTemplate = new RestTemplate();
      SSLContext sslContext = sslContextCreator.create(
          certificateBasedBasicAuth.getPfxFileContent(),
          certificateBasedBasicAuth.getPassphrase().toCharArray()
      );
      restTemplate.setRequestFactory(
          new HttpComponentsClientHttpRequestFactory(
              httpClientFactory.createNoHostnameVerifier(sslContext)
          )
      );
      return () -> restTemplate.exchange(requestEntityBuilder.build(), JsonNode.class);
    } catch (Exception e) {
      log.error(
          "Can't create HttpURLConnection for certificateBasedBasicAuth={}",
          certificateBasedBasicAuth
      );
      throw new IllegalStateException(e);
    }
  }

  @Override
  public Supplier<ResponseEntity<JsonNode>> visitOAuth2(OAuth2 oAuth2) {
    requestEntityBuilder.jwt(requireNonNull(oAuth2JWTProvider.get(oAuth2, Optional.empty())));
    var restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(
        new HttpComponentsClientHttpRequestFactory(
            httpClientFactory.createTrustAllAndNoHostnameVerifier()
        )
    );
    return () -> restTemplate.exchange(requestEntityBuilder.build(), JsonNode.class);
  }
}
