package tok.keycloak.authenticator;

import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.CredentialProviderFactory;
import org.keycloak.models.KeycloakSession;

public class EmailCredentialProviderFactory implements CredentialProviderFactory<EmailCredentialProvider> {

    public static final String PROVIDER_ID =  "tok-email";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public CredentialProvider<CredentialModel> create(KeycloakSession session) {
        return new EmailCredentialProvider(session);
    }
}
