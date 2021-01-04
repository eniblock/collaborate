package collaborate.login_event_listener;

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
    private final RealmProvider realmProvider;
    private RealmModel realm;
    private RoleModel idpAdminRoleModel;
    private DefaultEmailSenderProvider emailSenderProvider;

    private static final Logger log = Logger.getLogger(SendEmailEventListenerProvider.class);

    private String IDP_ADMIN_ROLE = System.getenv("IDP_ADMIN_ROLE");
    private String APPLICATION_NAME = System.getenv("APPLICATION_NAME");


    public SendEmailEventListenerProvider(KeycloakSession session) {
        this.session = session;
        this.realmProvider = session.realms();
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
        if(getRealm() == null) {
            this.realm = this.realmProvider.getRealm(event.getRealmId());
            idpAdminRoleModel = this.realm.getRole(IDP_ADMIN_ROLE);
        }

        if (EventType.REGISTER.equals(event.getType())) {
            log.info("New user is registered");
            UserModel newUser = this.session.users().getUserById(event.getUserId(), getRealm());

            sendNotificationEmailForIDPAdmins(newUser);
            sendNewAccountCreatedConfirmationEmailToUser(newUser);
        }
    }

    /**
     * Sending a notification email about a new user registration to all the idp admins
     *
     * @param newUser {Keycloak.UserModel} the new user that has been created
     */
    public void sendNotificationEmailForIDPAdmins(UserModel newUser) {
        ListIterator<UserModel> iterator = this.session.users().getUsers(getRealm()).listIterator();

        while(iterator.hasNext()) {
            UserModel user = iterator.next();

            if (user.hasRole(getIdpAdminRoleModel()) && user.getEmail() != null) {
                log.info("Sending notification email to idp admins: " + user.getEmail());

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
     * Sending a notification email about a new user registration to all the idp admins
     *
     * @param newUser {Keycloak.UserModel} the new user that has been created
     */
    public void sendNewAccountCreatedConfirmationEmailToUser(UserModel newUser) {
        try {
            this.getEmailSenderProvider().send(
                    session.getContext().getRealm().getSmtpConfig(),
                    newUser,
                    "Your account has been created",
                    "",
                    buildHTMLEmailContentForRegisterConfirmationEmail(newUser)
            );
        } catch (EmailException e) {
            log.error(e.getMessage());
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
                + this.APPLICATION_NAME + " PCC Platform.</p>";

        result += "Please validate this user";
        return result;
    }

    /**
     * Build a html template for the confirmation email, sent to the new user when he/she registers to the platform
     *
     * @param newUser {UserModel} - the UserModel instance which represent the new keycloak user is registered
     *
     * @return {String} the html template as string
     */
    private String buildHTMLEmailContentForRegisterConfirmationEmail(UserModel newUser) {
        String name = newUser.getFirstName() != null ? " " + newUser.getFirstName() : "";
        name += newUser.getLastName() != null ? " " + newUser.getLastName() : "";

        String result = "<p>Welcome<b>" + name + "</b> to <b>" + APPLICATION_NAME + " PCC Platform</b>,</p>";

        result += "<p>Your account has been successfully created. " +
                "You will receive a notification email when your <b>" + APPLICATION_NAME + " administrator</b> grants you access.</p>";

        return result;
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
    }

    @Override
    public void close() {
    }
}
