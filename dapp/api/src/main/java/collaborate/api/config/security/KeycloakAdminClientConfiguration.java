package collaborate.api.config.security;

import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Slf4j
@Configuration
@EnableConfigurationProperties(KeycloakAdminClientProperties.class)
public class KeycloakAdminClientConfiguration {

  private final KeycloakAdminClientProperties keycloakAdminClientProperties;

  @Bean
  public Keycloak keycloak() throws NoSuchAlgorithmException {
    final ResteasyClientBuilder resteasyClientBuilder =
        new ResteasyClientBuilder()
            .sslContext(SSLContext.getDefault());
    if (log.isDebugEnabled()) {
      log.debug("keycloakAdminClientProperties={}", keycloakAdminClientProperties);
    }

    if (!keycloakAdminClientProperties.isVerifyHostname()) {
      log.info("disableTrustManager");
      resteasyClientBuilder
          .disableTrustManager()
          .hostnameVerification(ResteasyClientBuilder.HostnameVerificationPolicy.ANY);
    }

    return KeycloakBuilder.builder()
        .resteasyClient(resteasyClientBuilder.build())
        .serverUrl(checkProperty(keycloakAdminClientProperties.getBaseUrl()))
        .realm(checkProperty(keycloakAdminClientProperties.getRealm()))
        .username(checkProperty(keycloakAdminClientProperties.getUser()))
        .password(checkProperty(keycloakAdminClientProperties.getPassword()))
        .clientId(checkProperty(keycloakAdminClientProperties.getClientId()))
        .clientSecret(checkProperty(keycloakAdminClientProperties.getClientSecret()))
        .grantType(checkProperty(keycloakAdminClientProperties.getGrantType()))
        .build();
  }

  @Bean
  public RealmResource realmResource() throws NoSuchAlgorithmException {
    return keycloak().realm(checkProperty(keycloakAdminClientProperties.getRealm()));
  }

  private String checkProperty(String property) {
    return (property == null || property.isEmpty()) ? null : property;
  }
}
