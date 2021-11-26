package tok.keycloak.authenticator;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.theme.Theme;
import org.keycloak.theme.ThemeProvider;

public class ConfigurationUtil {

  public static final String CAN_NOT_CONVERT = "Can not convert ";
  private static final Logger logger = Logger.getLogger(ConfigurationUtil.class);

  private ConfigurationUtil() {
  }

  public static String getAttributeValue(UserModel user, String attributeName) {
    String result = null;
    List<String> values = user.getAttribute(attributeName);
    if (values != null && !values.isEmpty()) {
      result = values.get(0);
    }

    return result;
  }

  public static String getConfigString(AuthenticatorConfigModel config, String configName) {
    return getConfigString(config, configName, null);
  }

  public static String getConfigString(AuthenticatorConfigModel config, String configName,
      String defaultValue) {

    String value = defaultValue;

    if (config.getConfig() != null) {
      // Get value
      value = config.getConfig().get(configName);
    }

    return value;
  }

  public static Long getConfigLong(AuthenticatorConfigModel config, String configName) {
    return getConfigLong(config, configName, null);
  }

  public static Long getConfigLong(AuthenticatorConfigModel config, String configName,
      Long defaultValue) {

    Long value = defaultValue;

    if (config == null) {
      logger.error("Config is null");
    } else if (config.getConfig() != null) {
      // Get value
      String obj = config.getConfig().get(configName);
      try {
        value = Long.valueOf(obj); // s --> ms
      } catch (NumberFormatException nfe) {
        logger.error(CAN_NOT_CONVERT + obj + " to a number.");
      }
    }

    return value;
  }

  public static Integer getConfigInteger(AuthenticatorConfigModel config, String configName) {
    return getConfigInteger(config, configName, null);
  }

  public static Integer getConfigInteger(AuthenticatorConfigModel config, String configName,
      Integer defaultValue) {

    Integer value = defaultValue;

    if (config == null) {
      logger.error("Config is null");
    } else if (config.getConfig() != null) {
      // Get value
      String obj = config.getConfig().get(configName);
      try {
        value = Integer.valueOf(obj); // s --> ms
      } catch (NumberFormatException nfe) {
        logger.error(CAN_NOT_CONVERT + obj + " to a number.");
      }
    }

    return value;
  }

  public static Boolean getConfigBoolean(AuthenticatorConfigModel config, String configName) {
    return getConfigBoolean(config, configName, true);
  }

  public static Boolean getConfigBoolean(AuthenticatorConfigModel config, String configName,
      Boolean defaultValue) {

    Boolean value = defaultValue;

    if (config.getConfig() != null) {
      // Get value
      String obj = config.getConfig().get(configName);
      try {
        value = Boolean.valueOf(obj); // s --> ms
      } catch (NumberFormatException nfe) {
        logger.error(CAN_NOT_CONVERT + obj + " to a boolean.");
      }
    }

    return value;
  }

  public static String createMessage(String text, String code, String mobileNumber) {
    if (text != null) {
      text = text.replace("%sms-code%", code);
      text = text.replace("%phonenumber%", mobileNumber);
    }
    return text;
  }

  public static String setDefaultCountryCodeIfZero(String mobileNumber, String prefix,
      String condition) {

    if (prefix != null && condition != null && mobileNumber.startsWith(condition)) {
      mobileNumber = prefix + mobileNumber.substring(1);
    }
    return mobileNumber;
  }

  public static String getMessage(AuthenticationFlowContext context, String key) {
    return getMessage(
        key,
        context.getSession(),
        context.getRealm(),
        context.getUser()
    );
  }

  private static String getMessage(String key, KeycloakSession session,
      RealmModel realm, UserModel user) {
    try {
      ThemeProvider themeProvider = session
          .getProvider(ThemeProvider.class, "extending");
      Theme currentTheme = themeProvider.getTheme(realm.getLoginTheme(),
          Theme.Type.LOGIN);
      Locale locale = session.getContext().resolveLocale(user);
      return currentTheme.getMessages(locale).getProperty(key);
    } catch (IOException e) {
      logger.warn(key + "not found in messages");
    }
    return null;
  }

  public static String getMessage(RequiredActionContext context, String key) {
    return ConfigurationUtil.getMessage(
        key,
        context.getSession(),
        context.getRealm(),
        context.getUser());
  }

}
