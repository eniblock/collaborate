package tok.keycloak.authenticator;

import java.util.ArrayList;
import java.util.List;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class EmailAuthenticatorFactory implements AuthenticatorFactory,
    ConfigurableAuthenticatorFactory {

  public static final String PROVIDER_ID = "tok-email-authenticator";
  private static final EmailAuthenticator SINGLETON = new EmailAuthenticator();

  @Override
  public String getId() {
    return PROVIDER_ID;
  }

  @Override
  public Authenticator create(KeycloakSession session) {
    return SINGLETON;
  }

  private static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
      AuthenticationExecutionModel.Requirement.REQUIRED,
      AuthenticationExecutionModel.Requirement.ALTERNATIVE,
      AuthenticationExecutionModel.Requirement.DISABLED
  };

  @Override
  public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
    return REQUIREMENT_CHOICES;
  }

  @Override
  public boolean isUserSetupAllowed() {
    return true;
  }

  @Override
  public boolean isConfigurable() {
    return true;
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return configProperties;
  }

  private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

  static {
    ProviderConfigProperty property;
    property = new ProviderConfigProperty();
    property.setName("cookie.max.age");
    property.setLabel("Cookie Max Age");
    property.setType(ProviderConfigProperty.STRING_TYPE);
    property.setHelpText("Max age in seconds of the SECRET_QUESTION_COOKIE.");

    // Code
    property = new ProviderConfigProperty();
    property.setName(Constants.CONF_MAIL_CODE_TTL);
    property.setLabel("Mail code time to live");
    property.setType(ProviderConfigProperty.STRING_TYPE);
    property.setHelpText("The validity of the sent code in minutes.");
    property.setDefaultValue(15);
    configProperties.add(property);

    property = new ProviderConfigProperty();
    property.setName(Constants.CONF_MAIL_CODE_LENGTH);
    property.setLabel("Length of the SMS code");
    property.setType(ProviderConfigProperty.STRING_TYPE);
    property.setHelpText("Length of the SMS code.");
    property.setDefaultValue(6);
    configProperties.add(property);
  }


  @Override
  public String getHelpText() {
    return "A mail is sent to user with code, copy-paste to authenticate";
  }

  @Override
  public String getDisplayType() {
    return "Email Code";
  }

  @Override
  public String getReferenceCategory() {
    return "Email Code";
  }

  @Override
  public void init(Config.Scope config) {
    // Not-used
  }

  @Override
  public void postInit(KeycloakSessionFactory factory) {
    // Not-used
  }

  @Override
  public void close() {
    // Not-used
  }


}
