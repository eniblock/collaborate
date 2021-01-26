package collaborate.api.config;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.restclient.OAuth2FeignRequestInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@EnableConfigurationProperties(ApiProperties.class)
public class FeignConfiguration {
    @Bean
    public OAuth2FeignRequestInterceptor oAuth2FeignRequestInterceptor(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {
        return new OAuth2FeignRequestInterceptor(
                clientRegistrationRepository,
                authorizedClientService,
                "catalog");
    }
}