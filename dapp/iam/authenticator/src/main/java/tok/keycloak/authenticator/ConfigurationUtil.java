package tok.keycloak.authenticator;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.RequiredActionContext;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.UserModel;
import org.keycloak.theme.Theme;
import org.keycloak.theme.ThemeProvider;

public class ConfigurationUtil {

    private static Logger logger = Logger.getLogger(ConfigurationUtil.class);

    public static String getAttributeValue(UserModel user, String attributeName) {
        String result = null;
        List<String> values = user.getAttribute(attributeName);
        if (values != null && values.size() > 0) {
            result = values.get(0);
        }

        return result;
    }

    public static String getConfigString(AuthenticatorConfigModel config, String configName) {
        return getConfigString(config, configName, null);
    }

    public static String getConfigString(AuthenticatorConfigModel config, String configName, String defaultValue) {

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

    public static Long getConfigLong(AuthenticatorConfigModel config, String configName, Long defaultValue) {

        Long value = defaultValue;

        if(config == null) {
        	 logger.error("Config is null");
        } else if (config.getConfig() != null) {
            // Get value
            Object obj = config.getConfig().get(configName);
            try {
                value = Long.valueOf((String) obj); // s --> ms
            } catch (NumberFormatException nfe) {
                logger.error("Can not convert " + obj + " to a number.");
            }
        }

        return value;
    }

    public static Integer getConfigInteger(AuthenticatorConfigModel config, String configName) {
        return getConfigInteger(config, configName, null);
    }

    public static Integer getConfigInteger(AuthenticatorConfigModel config, String configName, Integer defaultValue) {

    	Integer value = defaultValue;

        if(config == null) {
        	 logger.error("Config is null");
        } else if (config.getConfig() != null) {
            // Get value
            Object obj = config.getConfig().get(configName);
            try {
                value = Integer.valueOf((String) obj); // s --> ms
            } catch (NumberFormatException nfe) {
                logger.error("Can not convert " + obj + " to a number.");
            }
        }

        return value;
    }
    
    public static Boolean getConfigBoolean(AuthenticatorConfigModel config, String configName) {
        return getConfigBoolean(config, configName, true);
    }

    public static Boolean getConfigBoolean(AuthenticatorConfigModel config, String configName, Boolean defaultValue) {

        Boolean value = defaultValue;

        if (config.getConfig() != null) {
            // Get value
            Object obj = config.getConfig().get(configName);
            try {
                value = Boolean.valueOf((String) obj); // s --> ms
            } catch (NumberFormatException nfe) {
                logger.error("Can not convert " + obj + " to a boolean.");
            }
        }

        return value;
    }

    public static String createMessage(String text,String code, String mobileNumber) {
        if(text !=null){
            text = text.replaceAll("%sms-code%", code);
            text = text.replaceAll("%phonenumber%", mobileNumber);
        }
        return text;
    }

    public static String setDefaultCountryCodeIfZero(String mobileNumber,String prefix ,String condition) {

        if (prefix!=null && condition!=null && mobileNumber.startsWith(condition)) {
            mobileNumber = prefix + mobileNumber.substring(1);
        }
        return mobileNumber;
    }

    public static String getMessage(AuthenticationFlowContext context, String key){
        String result=null;
        try {
            ThemeProvider themeProvider = context.getSession().getProvider(ThemeProvider.class, "extending");
            Theme currentTheme = themeProvider.getTheme(context.getRealm().getLoginTheme(), Theme.Type.LOGIN);
            Locale locale = context.getSession().getContext().resolveLocale(context.getUser());
            result = currentTheme.getMessages(locale).getProperty(key);
        }catch (IOException e){
            logger.warn(key + "not found in messages");
        }
        return result;
    }

    public static String getMessage(RequiredActionContext context, String key){
        String result=null;
        try {
            ThemeProvider themeProvider = context.getSession().getProvider(ThemeProvider.class, "extending");
            Theme currentTheme = themeProvider.getTheme(context.getRealm().getLoginTheme(), Theme.Type.LOGIN);
            Locale locale = context.getSession().getContext().resolveLocale(context.getUser());
            result = currentTheme.getMessages(locale).getProperty(key);
        }catch (IOException e){
            logger.warn(key + "not found in messages");
        }
        return result;
    }

}
