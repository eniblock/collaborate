package collaborate.api.services.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "keycloak-admin-client-properties", ignoreUnknownFields = false)
public class KeycloakAdminClientProperties {

	private String baseUrl;
	private String realm;
	private String user;
	private String password;
	private String clientId;
	private String clientSecret;
	private String grantType;
	private boolean verifyHostname;
	
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getGrantType() {
		return grantType;
	}
	public void setGrantType(String grantType) {
		this.grantType = grantType;
	}
	public boolean getVerifyHostname() { return verifyHostname; }
	public void setVerifyHostname(boolean verifyHostname) { this.verifyHostname = verifyHostname; }
	
}
