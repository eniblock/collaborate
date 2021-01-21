package collaborate.api.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Arrays;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AuthorizationServerMetadata {
    private URI issuer;
    private URI authorizationEndpoint;
    @NotNull
    private URI tokenEndpoint;
    private String[] scopesSupported;
    private String[] responseTypesSupported;

    public URI getIssuer() {
        return issuer;
    }

    public void setIssuer(URI issuer) {
        this.issuer = issuer;
    }

    public URI getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(URI authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public URI getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(URI tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String[] getScopesSupported() {
        return scopesSupported;
    }

    public void setScopesSupported(String[] scopesSupported) {
        this.scopesSupported = scopesSupported;
    }

    public String[] getResponseTypesSupported() {
        return responseTypesSupported;
    }

    public void setResponseTypesSupported(String[] responseTypesSupported) {
        this.responseTypesSupported = responseTypesSupported;
    }

    @Override
    public String toString() {
        return "AuthorizationServerMetadata{" +
                "issuer=" + issuer +
                ", authorizationEndpoint=" + authorizationEndpoint +
                ", tokenEndpoint=" + tokenEndpoint +
                ", scopesSupported=" + Arrays.toString(scopesSupported) +
                ", responseTypesSupported=" + Arrays.toString(responseTypesSupported) +
                '}';
    }
}
