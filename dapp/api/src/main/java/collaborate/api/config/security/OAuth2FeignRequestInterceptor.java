package collaborate.api.config.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import java.util.Objects;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public class OAuth2FeignRequestInterceptor implements RequestInterceptor {

  private static final Authentication ANONYMOUS_AUTHENTICATION = new AnonymousAuthenticationToken(
      "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

  private final OAuth2AuthorizedClientManager authorizedClientManager;
  private final String clientRegistrationId;

  public OAuth2FeignRequestInterceptor(
      final ClientRegistrationRepository clientRegistrationRepository,
      final OAuth2AuthorizedClientService authorizedClientService,
      final String clientRegistrationId) {
    this.clientRegistrationId = clientRegistrationId;
    this.authorizedClientManager = buildAuthorizedClientManager(
        clientRegistrationRepository,
        authorizedClientService
    );
  }

  private AuthorizedClientServiceOAuth2AuthorizedClientManager buildAuthorizedClientManager(
      ClientRegistrationRepository clientRegistrationRepository,
      OAuth2AuthorizedClientService authorizedClientService) {
    AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedOAuth2ClientManager =
        new AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientService);
    authorizedOAuth2ClientManager.setAuthorizedClientProvider(
        OAuth2AuthorizedClientProviderBuilder
            .builder()
            .clientCredentials()
            .build()
    );
    return authorizedOAuth2ClientManager;
  }

  @Override
  public void apply(RequestTemplate template) {
    if (this.authorizedClientManager == null) {
      return;
    }

    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
        .withClientRegistrationId(this.clientRegistrationId)
        .principal(ANONYMOUS_AUTHENTICATION)
        .build();

    OAuth2AuthorizedClient authorizedClient = this.authorizedClientManager
        .authorize(authorizeRequest);
    OAuth2AccessToken accessToken = Objects.requireNonNull(authorizedClient).getAccessToken();
    template
        .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken.getTokenValue()));
  }

}
