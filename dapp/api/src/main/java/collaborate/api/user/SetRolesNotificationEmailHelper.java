package collaborate.api.user;

import collaborate.api.mail.EMailDTO;
import java.util.HashMap;
import java.util.Map;

public class SetRolesNotificationEmailHelper {

  private final Map<String, String> rolesNamesMap;

  public SetRolesNotificationEmailHelper(String idpAdminRole) {
    rolesNamesMap = new HashMap<>();
    rolesNamesMap.put("service_provider_operator", "Operator");
    rolesNamesMap.put("service_provider_administrator", "Administrator");
    rolesNamesMap.put(idpAdminRole, "Identity Provider Admin");
  }

  public EMailDTO buildRolesSetNotificationEmail(
      String from,
      String to,
      String firstName,
      String lastName,
      String[] roles,
      String platform
  ) {
    String subject = "Your roles have been modified";
    return new EMailDTO(
        from,
        to,
        subject,
        Map.of(
            "content", buildContent(roles, platform),
            "greeting", greeting(firstName, lastName)
        )
    );
  }

  private String greeting(String firstName, String lastName) {
    String name = firstName != null ? " " + firstName : "";
    name += lastName != null ? " " + lastName : "";

    return "<p>Hello<b>" + name + "</b>,</p>";
  }

  private String buildContent(String[] roles, String platform) {
    var contentBuffer = new StringBuilder(
        "Your account has been modified by <b>" + platform + " administrator</b>.");

    if (roles.length == 0) {
      contentBuffer.append(" You now have no roles.");
      return contentBuffer.toString();
    }

    contentBuffer.append(" You now have the following role(s): ");

    for (String role : roles) {
      String userFriendlyName = rolesNamesMap.get(role);

      if (userFriendlyName != null) {
        contentBuffer.append(rolesNamesMap.get(role))
            .append(", ");
      }
    }

    if (contentBuffer.lastIndexOf(", ") == contentBuffer.length() - 2) {
      contentBuffer = new StringBuilder(contentBuffer.substring(0, contentBuffer.length() - 2))
          .append(".");
    }

    return contentBuffer.toString();
  }
}
