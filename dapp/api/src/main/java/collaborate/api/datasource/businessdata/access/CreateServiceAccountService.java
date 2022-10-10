package collaborate.api.datasource.businessdata.access;

import collaborate.api.datasource.businessdata.access.model.ClientIdAndSecret;
import collaborate.api.datasource.model.dto.web.authentication.transfer.OAuth2ClientCredentials;
import collaborate.api.http.HttpClientFactory;
import collaborate.api.http.RequestEntityBuilder;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateServiceAccountService {

  private final HttpClientFactory httpClientFactory;

  public ResponseEntity<ClientIdAndSecret> post(OAuth2ClientCredentials transferMethod,
      Optional<String> scope, String jwt) {
    var registrationURL = transferMethod.getRegistrationURL();
    if (scope.isPresent()) {
      registrationURL += Arrays.stream(scope.get().split(" "))
          .collect(Collectors.joining("&scope=", "?scope=", ""));
    }

    log.debug("====> " + registrationURL);

    var requestEntity = new RequestEntityBuilder<>(registrationURL)
        .jwt(jwt)
        .requestMethod(RequestMethod.POST.toString())
        .build();

    try {
      RestTemplate restTemplate = createRestTemplate();
      return restTemplate.exchange(requestEntity, ClientIdAndSecret.class);
    } catch (RestClientException e) {
      log.error("Can't create service-account", e);
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
          "HTTP error while creating the service account", e);
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
