package collaborate.login_event_listner;

import org.jboss.logging.Logger;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.*;

import java.util.*;

public class SendEmailEventListenerProvider implements EventListenerProvider {
    private final KeycloakSession session;
    private final RealmProvider model;
    private RealmModel realm;
    private RoleModel idpAdminRoleModel;
    private DefaultEmailSenderProvider emailSenderProvider;

    private static final Logger log = Logger.getLogger(SendEmailEventListenerProvider.class);

    private String IDP_ADMIN_ROLE = "service_identity_provider_administrator";


    public SendEmailEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.model = session.realms();
        this.emailSenderProvider = new DefaultEmailSenderProvider(this.session);
    }

    public DefaultEmailSenderProvider getEmailSenderProvider() {
        return this.emailSenderProvider;
    }

    public RealmModel getRealm() {
        return this.realm;
    }

    public RoleModel getIdpAdminRoleModel() {
        return this.idpAdminRoleModel;
    }

    @Override
    public void onEvent(Event event) {
        if(this.realm == null) {
            this.realm = this.model.getRealm(event.getRealmId());
            idpAdminRoleModel = this.realm.getRole(IDP_ADMIN_ROLE);
        }

        if (EventType.REGISTER.equals(event.getType())) {
            log.info("New user is registered");
            sendNotificationEmailForIDPAdmins(event);
        }
    }

    /**
     * Sending a notification email about a new user registration to all the idp admins
     *
     * @param event {Keycloak.Events} the REGISTER event that happened
     */
    public void sendNotificationEmailForIDPAdmins(Event event) {
        ListIterator<UserModel> iterator = this.session.users().getUsers(getRealm()).listIterator();

        while(iterator.hasNext()) {
            UserModel user = iterator.next();

            if (user.hasRole(getIdpAdminRoleModel()) && user.getEmail() != null) {
                log.info("Sending email to " + user.getEmail());

                UserModel newUser = this.session.users().getUserById(event.getUserId(), getRealm());

                try {
                    this.getEmailSenderProvider().send(
                        session.getContext().getRealm().getSmtpConfig(),
                        user,
                        "New User Created!",
                        "",
                        buildHTMLEmailContentForNotificationEmail(newUser)
                    );
                } catch (EmailException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * Build a html template for the notification email which will be sent to idp admins
     *
     * @param newUser {UserModel} - the UserModel instance which represent the new keycloak user is registered
     *
     * @return {String} the html template as string
     */
    private String buildHTMLEmailContentForNotificationEmail(UserModel newUser) {
        String result = "<p>Hello,</p>";

        result += "<p><b>" + newUser.getFirstName() + " " + newUser.getLastName()
                + "</b> has created a new account on "
                + System.getenv("APPLICATION_NAME") + " PCC Platform.</p>";

        result += "Please validate this user";
        return result;
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
    }

    @Override
    public void close() {
    }
}
