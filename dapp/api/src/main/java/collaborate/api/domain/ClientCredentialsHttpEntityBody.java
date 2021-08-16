package collaborate.api.domain;

import collaborate.api.datasource.security.OAuth2ClientSecret;
import collaborate.api.domain.enumeration.GrantType;
import org.springframework.util.LinkedMultiValueMap;

public class ClientCredentialsHttpEntityBody extends LinkedMultiValueMap<String, String> {
    public ClientCredentialsHttpEntityBody(OAuth2ClientSecret OAuth2ClientSecret) {
        this.add("grant_type", GrantType.client_credentials.toString());
        this.add("client_id", OAuth2ClientSecret.getClientId());
        this.add("client_secret", OAuth2ClientSecret.getClientSecret());
    }

    public ClientCredentialsHttpEntityBody(OAuth2ClientSecret OAuth2ClientSecret, String scope) {
        this.add("grant_type", GrantType.client_credentials.toString());
        this.add("client_id", OAuth2ClientSecret.getClientId());
        this.add("client_secret", OAuth2ClientSecret.getClientSecret());
        this.add("scope", scope);
    }
}
