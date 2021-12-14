package collaborate.api.datasource;

import collaborate.api.datasource.gateway.AccessTokenProvider;
import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public class RequestEntityVisitor implements
    AuthenticationVisitor<Supplier<ResponseEntity<JsonNode>>> {

  @Nullable
  private final String authenticationScope;
  private final AccessTokenProvider accessTokenProvider;
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
  public Supplier<ResponseEntity<JsonNode>> visitOAuth2(OAuth2ClientCredentialsGrant oAuth2) {
    requestEntityBuilder.jwt(
        accessTokenProvider.get(
            oAuth2,
            Optional.ofNullable(authenticationScope)
        )
    );
    var restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(
        new HttpComponentsClientHttpRequestFactory(
            httpClientFactory.createTrustAllAndNoHostnameVerifier()
        )
    );
    return () -> restTemplate.exchange(requestEntityBuilder.build(), JsonNode.class);
  }
}
