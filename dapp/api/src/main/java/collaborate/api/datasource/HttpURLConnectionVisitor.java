package collaborate.api.datasource;

import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2;
import collaborate.api.datasource.model.dto.web.authentication.OpenIdConfiguration;
import collaborate.api.http.HttpClientFactory;
import collaborate.api.http.HttpURLConnectionBuilder;
import collaborate.api.http.security.SSLContextFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Objects;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
public class HttpURLConnectionVisitor implements AuthenticationVisitor<HttpURLConnection> {

  private final HttpURLConnectionBuilder httpURLConnectionBuilder;
  private final HttpClientFactory httpClientFactory;
  private final SSLContextFactory sslContextCreator;

  @Override
  public HttpURLConnection visitBasicAuth(BasicAuth basicAuth) {
    addBasicAuth(basicAuth);
    try {
      return httpURLConnectionBuilder.build();
    } catch (IOException e) {
      log.error("While initializing basicAuth={}", basicAuth);
      throw new IllegalStateException(e);
    }
  }

  private void addBasicAuth(BasicAuth basicAuth) {
    httpURLConnectionBuilder
        .authorizationBasic(basicAuth.getUser(), basicAuth.getPassword());
  }

  @Override
  public HttpURLConnection visitCertificateBasedBasicAuth(
      CertificateBasedBasicAuth certificateBasedBasicAuth) {
    addBasicAuth(certificateBasedBasicAuth);
    SSLContext sslContext;
    try {
      sslContext = sslContextCreator.create(
          certificateBasedBasicAuth.getPfxFileContent(),
          certificateBasedBasicAuth.getPassphrase().toCharArray()
      );
      return httpURLConnectionBuilder
          .sslContext(sslContext)
          .build();
    } catch (Exception e) {
      log.error(
          "Can't create HttpURLConnection for certificateBasedBasicAuth={}",
          certificateBasedBasicAuth
      );
      throw new IllegalStateException(e);
    }
  }

  @Override
  public HttpURLConnection visitOAuth2(OAuth2 oAuth2) {
    var openIdConfigurationUrl = UriComponentsBuilder
        .fromUriString(
            oAuth2.getIssuerIdentifierUri() + "/" + oAuth2.getWellKnownURIPathSuffix()
        ).build()
        .toUri();
    try {
      var restTemplate = new RestTemplate();
      restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(
          httpClientFactory.createTrustAllAndNoHostnameVerifier()));
      var openIdConfiguration = restTemplate
          .getForObject(openIdConfigurationUrl, OpenIdConfiguration.class);

      var entityBody = new HashMap<String, String>();
      entityBody.put("grant_type", oAuth2.getGrantType());
      entityBody.put("client_id", oAuth2.getClientId());
      entityBody.put("client_secret", oAuth2.getClientSecret());

      return new HttpURLConnectionBuilder(
          Objects.requireNonNull(openIdConfiguration).getTokenEndpoint().toString())
          .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString())
          .requestMethod("POST")
          .body(entityBody)
          .build();
    } catch (IOException e) {
      log.error("Can't create HttpURLConnection for oAuth2={}", oAuth2);
      throw new IllegalStateException(e);
    }

  }


}
