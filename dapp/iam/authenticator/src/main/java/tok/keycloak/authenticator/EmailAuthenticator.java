package tok.keycloak.authenticator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.common.util.RandomString;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.email.EmailException;
import org.keycloak.email.EmailTemplateProvider;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

public class EmailAuthenticator implements Authenticator /*, CredentialValidator<EmailCredentialProvider>*/ {

	private static Logger logger = Logger.getLogger(EmailAuthenticator.class);
	
    @Override
    public void authenticate(AuthenticationFlowContext context) {        
        Integer codeLength = ConfigurationUtil.getConfigInteger(context.getAuthenticatorConfig(), Constants.CONF_MAIL_CODE_LENGTH);
        
        // TODO v0.1.0 Check if "RandomString.randomCode(8);" should work AND delete RandomString if it is ok
        String code = new RandomString(codeLength).nextString();

        EmailTemplateProvider emailTemplateProvider = context.getSession().getProvider(EmailTemplateProvider.class);
        LoginFormsProvider forms = context.form();
        
        storeSMSCode(context, code);
        
        String template = Constants.TEMPLATE_EMAIL;
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("code", code);
        try {
            emailTemplateProvider
	            .setRealm(context.getRealm())
	            .setUser(context.getUser())
	            .send(Constants.TEMPLATE_SUBJECT, template, attributes);
        } catch (final EmailException e) {
            forms.setError(e.getMessage());
        }
        
        Response challenge = forms.createForm(Constants.TEMPLATE_LOGIN_PAGE);
        context.challenge(challenge);
    }
    
    private void storeSMSCode(AuthenticationFlowContext context, String code) {
        UserCredentialModel credentials = new UserCredentialModel();
        credentials.setType(Constants.TYPE_MAIL_CODE);
        credentials.setValue(code);
        context.getSession().userCredentialManager().updateCredential(context.getRealm(), context.getUser(), credentials);
    }
    
    private enum CODE_STATUS {
        VALID,
        INVALID,
        EXPIRED
    }
    
    protected CODE_STATUS validateCode(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String enteredCode = formData.getFirst(Constants.TEMPLATE_HTML_NAME);
        KeycloakSession session = context.getSession();

        List<CredentialModel> codeCreds = session.userCredentialManager().getStoredCredentialsByType(context.getRealm(), context.getUser(), Constants.TYPE_MAIL_CODE);

        if(codeCreds.isEmpty()) {
        	return CODE_STATUS.INVALID;
        }
        
        CredentialModel credentialModel = codeCreds.get(0);
        Long createdDate = credentialModel.getCreatedDate();
        String secretData = credentialModel.getSecretData();
        Long currentTimeMillis = Time.currentTimeMillis();
        Long ms = (currentTimeMillis - createdDate) / (60L * 1000L);
        
        Long codeTTLMinute = ConfigurationUtil.getConfigLong(context.getAuthenticatorConfig(), Constants.CONF_MAIL_CODE_TTL);
        
        if(enteredCode == null || credentialModel == null || !enteredCode.equals(secretData)) {
        	return CODE_STATUS.INVALID;
        } else if(ms > codeTTLMinute) {
        	return CODE_STATUS.EXPIRED;
        } else {
        	return CODE_STATUS.VALID;
        }
    }
    
    @Override
    public void action(AuthenticationFlowContext context) {
        logger.debug("action called ... context = " + context);
        CODE_STATUS status = validateCode(context);
        Response challenge = null;
        switch (status) {
            case EXPIRED:
                challenge = context.form()
                        .setError("code-auth.code.expired")
                        .createForm(Constants.TEMPLATE_LOGIN_PAGE);
                context.failureChallenge(AuthenticationFlowError.EXPIRED_CODE, challenge);
                break;
            case INVALID:
                if (context.getExecution().getRequirement() == AuthenticationExecutionModel.Requirement.CONDITIONAL ||
                        context.getExecution().getRequirement() == AuthenticationExecutionModel.Requirement.ALTERNATIVE) {
                    logger.debug("Calling context.attempted()");
                    context.attempted();
                } else if (context.getExecution().getRequirement() == AuthenticationExecutionModel.Requirement.REQUIRED) {
                    challenge = context.form()
                            .setError("code-auth.code.invalid")
                            .createForm(Constants.TEMPLATE_LOGIN_PAGE);
                    context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
                } else {
                    logger.warn("Undefined execution ...");
                }
                break;
            case VALID:
                context.success();
                break;
        }
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    public List<RequiredActionFactory> getRequiredActions(KeycloakSession session) {
        return Collections.emptyList();
    }

    @Override
    public void close() {
    }

}
