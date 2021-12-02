package collaborate.api.datasource.gateway;

import static java.util.Objects.requireNonNull;

import collaborate.api.datasource.model.dto.web.authentication.AccessTokenResponse;
import collaborate.api.datasource.model.dto.web.authentication.OAuth2ClientCredentialsGrant;
import collaborate.api.datasource.model.dto.web.authentication.OpenIdConfiguration;
import collaborate.api.http.HttpClientFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenProvider {

  private final HttpClientFactory httpClientFactory;

  public AccessTokenResponse get(OAuth2ClientCredentialsGrant oAuth2, Optional<String> scope) {
    var openIdConfigurationUrl = UriComponentsBuilder
        .fromUriString(
            oAuth2.getIssuerIdentifierUri() + "/" + oAuth2.getWellKnownURIPathSuffix()
        ).build()
        .toUri();
    try {
      var restTemplate = new RestTemplate();
      restTemplate.setRequestFactory(
          new HttpComponentsClientHttpRequestFactory(
              httpClientFactory.createTrustAllAndNoHostnameVerifier()
          )
      );
      var openIdConfiguration = restTemplate.getForObject(
          openIdConfigurationUrl,
          OpenIdConfiguration.class
      );

      var entityBody = new LinkedMultiValueMap<String, String>();
      entityBody.add("grant_type", oAuth2.getGrantType());
      entityBody.add("client_id", oAuth2.getClientId());
      entityBody.add("client_secret", oAuth2.getClientSecret());
      scope.ifPresent(s -> entityBody.add("scope", s));
      // Get token
      return restTemplate.postForEntity(
          requireNonNull(openIdConfiguration).getTokenEndpoint().toString(),
          entityBody,
          AccessTokenResponse.class
      ).getBody();
    } catch (RestClientException e) {
      log.error("Can't get JWT for oAuth2={}", oAuth2);
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "HTTP error while getting JWT", e);
    }
  }
}
