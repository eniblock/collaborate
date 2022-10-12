package collaborate.api.datasource;

import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.AuthenticationVisitor;
import collaborate.api.datasource.model.dto.web.authentication.BasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.CertificateBasedBasicAuth;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.http.BasicAuthHeader;
import collaborate.api.http.HttpClientFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationBearerVisitor implements AuthenticationVisitor<String> {

  private final HttpClientFactory httpClientFactory;
  private final Optional<String> scope;

  @Override
  public String visitBasicAuth(BasicAuth basicAuth) {
    return new BasicAuthHeader(basicAuth.getUser(), basicAuth.getPassword()).getValue();
  }

  @Override
  public String visitCertificateBasedBasicAuth(CertificateBasedBasicAuth basicAuth) {
    return this.visitBasicAuth(basicAuth);
  }

  @Override
  public String visitOAuth2(OAuth2ClientCredentialsGrant oAuth2) {
    try {
      RestTemplate restTemplate = createRestTemplate();
      var entityBody = oAuth2.toEntityBody();
      scope.ifPresent(s -> entityBody.add("scope", s));
      // Get token
      return restTemplate.postForEntity(
          oAuth2.getTokenEndpoint().toString(),
          entityBody,
          AccessTokenResponse.class
      ).getBody().getBearerHeaderValue();
    } catch (RestClientException e) {
      log.error("Can't get JWT for oAuth2={}, exception={}", oAuth2, e);
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "HTTP error while getting JWT", e);
    }
  }


  private RestTemplate createRestTemplate() {
    var restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(
        new HttpComponentsClientHttpRequestFactory(
            httpClientFactory.createTrustAllAndNoHostnameVerifier()
        )
    );
    return restTemplate;
  }
}
