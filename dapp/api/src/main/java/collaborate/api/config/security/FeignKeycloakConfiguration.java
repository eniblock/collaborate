package collaborate.api.config.security;

import collaborate.api.restclient.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

public class FeignKeycloakConfiguration {

  @Bean
  public OAuth2FeignRequestInterceptor keycloakInterceptor(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientService authorizedClientService) {
    return new OAuth2FeignRequestInterceptor(
        clientRegistrationRepository,
        authorizedClientService,
        "collaborate");
  }
}