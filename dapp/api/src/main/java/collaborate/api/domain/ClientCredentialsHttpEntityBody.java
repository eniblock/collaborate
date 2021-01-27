package collaborate.api.domain;

import collaborate.api.domain.enumeration.GrantType;
import org.springframework.util.LinkedMultiValueMap;

public class ClientCredentialsHttpEntityBody extends LinkedMultiValueMap<String, String> {
    public ClientCredentialsHttpEntityBody(Datasource datasource) {
        this.add("grant_type", GrantType.client_credentials.toString());
        this.add("client_id", datasource.getClientId());
        this.add("client_secret", datasource.getClientSecret());
    }
}
