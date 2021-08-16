package collaborate.api.domain;

import collaborate.api.datasource.domain.DatasourceClientSecret;
import collaborate.api.domain.enumeration.GrantType;
import org.springframework.util.LinkedMultiValueMap;

public class ClientCredentialsHttpEntityBody extends LinkedMultiValueMap<String, String> {
    public ClientCredentialsHttpEntityBody(DatasourceClientSecret datasourceClientSecret) {
        this.add("grant_type", GrantType.client_credentials.toString());
        this.add("client_id", datasourceClientSecret.getClientId());
        this.add("client_secret", datasourceClientSecret.getClientSecret());
    }

    public ClientCredentialsHttpEntityBody(DatasourceClientSecret datasourceClientSecret, String scope) {
        this.add("grant_type", GrantType.client_credentials.toString());
        this.add("client_id", datasourceClientSecret.getClientId());
        this.add("client_secret", datasourceClientSecret.getClientSecret());
        this.add("scope", scope);
    }
}
