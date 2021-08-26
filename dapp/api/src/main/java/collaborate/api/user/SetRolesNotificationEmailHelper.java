package collaborate.api.user;

import collaborate.api.mail.MailDTO;
import java.util.HashMap;
import java.util.Map;

public class SetRolesNotificationEmailHelper {
    private Map<String, String> rolesNamesMap;

    public SetRolesNotificationEmailHelper(String idpAdminRole) {
        rolesNamesMap = new HashMap<>();
        rolesNamesMap.put("service_provider_operator", "Operator");
        rolesNamesMap.put("service_provider_administrator", "Administrator");
        rolesNamesMap.put(idpAdminRole, "Identity Provider Admin");
    }

    public MailDTO buildRolesSetNotificationEmail(
            String from,
            String to,
            String firstName,
            String lastName,
            String[] roles,
            String platform
    ) {
        String subject = "Your roles have been modified";
        return new MailDTO(
            from,
            to,
            subject,
            content(roles, platform),
            greeting(firstName, lastName)
        );
    }

    private String greeting(String firstName, String lastName) {
        String name = firstName != null ? " " + firstName : "";
        name += lastName != null ? " " + lastName : "";

        return "<p>Hello<b>" + name + "</b>,</p>";
    }

    private String content(String[] roles, String platform) {
        String result = "Your account has been modified by <b>" +  platform + " administrator</b>.";

        if (roles.length == 0) {
            result += " You now have no roles.";
            return result;
        }

        result += " You now have the following role(s): ";

        for (String role: roles) {
            String userFriendlyName = rolesNamesMap.get(role);

            if (userFriendlyName != null) {
                result += rolesNamesMap.get(role) + ", ";
            }
        }

        if (result.lastIndexOf(", ") == result.length() - 2) {
            result = result.substring(0, result.length() - 2) + ".";
        }

        return result;
    }
}
