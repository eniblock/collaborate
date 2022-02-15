package collaborate.api.datasource.gateway;

import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.http.HttpClientFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenProvider {

  private final HttpClientFactory httpClientFactory;

  public AccessTokenResponse get(OAuth2ClientCredentialsGrant oAuth2, Optional<String> scope) {
    try {
      RestTemplate restTemplate = createRestTemplate();
      var entityBody = oAuth2.toEntityBody();
      scope.ifPresent(s -> entityBody.add("scope", s));
      // Get token
      return restTemplate.postForEntity(
          oAuth2.getTokenEndpoint().toString(),
          entityBody,
          AccessTokenResponse.class
      ).getBody();
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
