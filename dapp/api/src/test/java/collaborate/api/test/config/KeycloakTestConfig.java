package collaborate.api.test.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"test"})
public class KeycloakTestConfig {

  @Bean
  public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }
}
