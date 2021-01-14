package collaborate.api.services.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableConfigurationProperties(KeycloakAdminClientProperties.class)
public class KeycloakAdminClientConfiguration {
	
	@Autowired
	private KeycloakAdminClientProperties keycloakAdminClientProperties;

	@Bean
	public Keycloak keycloak() throws NoSuchAlgorithmException {
		final ResteasyClientBuilder resteasyClientBuilder =
				new ResteasyClientBuilder()
						.sslContext(SSLContext.getDefault());
		if (!keycloakAdminClientProperties.getVerifyHostname()) {
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
