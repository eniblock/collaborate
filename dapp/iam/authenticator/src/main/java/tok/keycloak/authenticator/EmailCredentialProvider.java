package tok.keycloak.authenticator;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jboss.logging.Logger;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.CredentialTypeMetadata;
import org.keycloak.credential.CredentialTypeMetadataContext;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

public class EmailCredentialProvider 
		implements CredentialProvider<CredentialModel>, CredentialInputUpdater {
	
    @SuppressWarnings("unused")
	  private static final Logger logger = Logger.getLogger(EmailCredentialProvider.class);
	    
    protected KeycloakSession session;

    public EmailCredentialProvider(KeycloakSession session) {
        this.session = session;
    }
    
    private UserCredentialStore getCredentialStore() {
        return session.userCredentialManager();
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return Constants.TYPE_MAIL_CODE.equals(credentialType);
    }

    @Override
    public CredentialModel createCredential(RealmModel realm, UserModel user, CredentialModel credentialModel) {
        if (credentialModel.getCreatedDate() == null) {
            credentialModel.setCreatedDate(Time.currentTimeMillis());
        }
        return getCredentialStore().createCredential(realm, user, credentialModel);
    }

    @Override
    public boolean deleteCredential(RealmModel realm, UserModel user, String credentialId) {
        return getCredentialStore().removeStoredCredential(realm, user, credentialId);
    }

    @Override
    public CredentialModel getCredentialFromModel(CredentialModel model) {
        return model;
    }

    @Override
    public CredentialTypeMetadata getCredentialTypeMetadata(CredentialTypeMetadataContext metadataContext) {
        return CredentialTypeMetadata.builder()
                .type(getType())
                .category(CredentialTypeMetadata.Category.TWO_FACTOR)
                .displayName(EmailCredentialProviderFactory.PROVIDER_ID)
                .helpText("secret-question-text")
                .createAction(EmailAuthenticatorFactory.PROVIDER_ID)
                .removeable(false)
                .build(session);
    }

    @Override
    public String getType() {
        return Constants.TYPE_MAIL_CODE;
    }
    
    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
        if (!Constants.TYPE_MAIL_CODE.equals(input.getType())) return false;
        if (!(input instanceof UserCredentialModel)) return false;
        UserCredentialModel credInput = (UserCredentialModel) input;
        List<CredentialModel> creds = session.userCredentialManager().getStoredCredentialsByType(realm, user, Constants.TYPE_MAIL_CODE);
        if (creds.isEmpty()) {
            CredentialModel secret = new CredentialModel();
            secret.setType(Constants.TYPE_MAIL_CODE);
            secret.setSecretData(credInput.getValue());
            secret.setCreatedDate(Time.currentTimeMillis());
            session.userCredentialManager().createCredential(realm, user, secret);
        } else {
            creds.get(0).setSecretData(credInput.getValue());
            creds.get(0).setCreatedDate(Time.currentTimeMillis());
            session.userCredentialManager().updateCredential(realm, user, creds.get(0));
        }
        session.userCache().evict(realm, user);
        return true;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        if (!Constants.TYPE_MAIL_CODE.equals(credentialType)) return;
        session.userCredentialManager().disableCredentialType(realm, user, credentialType);
        session.userCache().evict(realm, user);

    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        if (!session.userCredentialManager().getStoredCredentialsByType(realm, user, Constants.TYPE_MAIL_CODE).isEmpty()) {
            Set<String> set = new HashSet<>();
            set.add(Constants.TYPE_MAIL_CODE);
            return set;
        } else {
            return Collections.<String>emptySet();
        }

    }    
}
