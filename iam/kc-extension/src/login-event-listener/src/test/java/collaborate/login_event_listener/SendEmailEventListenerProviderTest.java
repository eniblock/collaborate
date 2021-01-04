package collaborate.login_event_listener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.email.DefaultEmailSenderProvider;
import org.keycloak.email.EmailException;
import org.keycloak.events.Event;
import org.keycloak.events.EventType;
import org.keycloak.models.*;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


public class SendEmailEventListenerProviderTest {
    @Mock
    private KeycloakSession session;

    @Mock
    private RealmProvider realmProvider;

    @Mock
    private RealmModel realmModel;

    @Mock
    private Event event;

    @Mock
    private UserProvider userProvider;

    private String FAKE_REALM_ID = "fake_id";
    private String IDP_ADMIN_ROLE = System.getenv("IDP_ADMIN_ROLE");
    private String APPLICATION_NAME = System.getenv("APPLICATION_NAME");
    private String fakeNewUserId = "fake_id";

    @Before
    public void beforeEach() {
        session = mock(KeycloakSession.class);
        realmProvider = mock(RealmProvider.class);
        realmModel = mock(RealmModel.class);
        event = mock(Event.class);
        userProvider = mock(UserProvider.class);
    }

    @After
    public void afterEach() {
        reset(session);
        reset(realmProvider);
        reset(realmModel);
        reset(event);
        reset(userProvider);
    }
    @Test
    public void onEventSetRealmAndWhenEventIsNotRegister() {
        UserModel userModel = mock(UserModel.class);

        when(event.getRealmId()).thenReturn(FAKE_REALM_ID);
        when(event.getType()).thenReturn(EventType.LOGOUT);

        when(realmProvider.getRealm(FAKE_REALM_ID)).thenReturn(realmModel);

        when(session.realms()).thenReturn(realmProvider);

        when(userProvider.getUserById(fakeNewUserId, realmModel)).thenReturn(userModel);

        SendEmailEventListenerProvider provider = new SendEmailEventListenerProvider(session);

        // when call onEvent without realmModel is set
        provider.onEvent(event);

        // then
        verify(realmProvider, times(1)).getRealm(FAKE_REALM_ID);
        verify(realmModel, times(1)).getRole(IDP_ADMIN_ROLE);

        // when call onEvent again, realmModel is already set
        provider.onEvent(event);

        // should not call getRealm and getRole again
        verify(realmProvider, times(1)).getRealm(FAKE_REALM_ID);
        verify(realmModel, times(1)).getRole(IDP_ADMIN_ROLE);

        // should not get the users because the event type is not REGISTER
        verify(session,times(0)).users();
    }

    @Test
    public void onEventWhenEventIsRegister() {
        UserModel userModel = mock(UserModel.class);

        when(event.getRealmId()).thenReturn(FAKE_REALM_ID);
        when(event.getType()).thenReturn(EventType.REGISTER);
        when(event.getUserId()).thenReturn(fakeNewUserId);

        when(session.realms()).thenReturn(realmProvider);
        when(session.users()).thenReturn(userProvider);

        when(realmProvider.getRealm(FAKE_REALM_ID)).thenReturn(realmModel);

        when(userProvider.getUserById(fakeNewUserId, realmModel)).thenReturn(userModel);

        SendEmailEventListenerProvider provider = new SendEmailEventListenerProvider(session);

         SendEmailEventListenerProvider spyProvider = spy(provider);
         when(spyProvider.getRealm()).thenReturn(realmModel);
         doNothing().when(spyProvider).sendNotificationEmailForIDPAdmins(userModel);
         doNothing().when(spyProvider).sendNewAccountCreatedConfirmationEmailToUser(userModel);

         // when call onEvent with event type is Register
         spyProvider.onEvent(event);

         // then
         verify(spyProvider, times(1)).sendNotificationEmailForIDPAdmins(userModel);
         verify(spyProvider, times(1)).sendNewAccountCreatedConfirmationEmailToUser(userModel);
    }

    @Test
    public void sendNotificationEmailForIDPAdmins() {
        // Mock all objects required for the function
        DefaultEmailSenderProvider emailSenderProvider = mock(DefaultEmailSenderProvider.class);
        KeycloakContext keycloakContext = mock(KeycloakContext.class);
        Map<String,String> fakeSmtpConfig = new HashMap<>();
        RoleModel idpAdminRole = mock(RoleModel.class);

        List<UserModel> listUsers = createTestUsers();

        UserModel newUser = new TestUserModel(fakeNewUserId, "Toto", "Tata");

        when(session.realms()).thenReturn(realmProvider);
        when(session.getContext()).thenReturn(keycloakContext);
        when(session.users()).thenReturn(userProvider);

        when(keycloakContext.getRealm()).thenReturn(realmModel);

        when(realmModel.getSmtpConfig()).thenReturn(fakeSmtpConfig);

        when(userProvider.getUsers(realmModel)).thenReturn(listUsers);
        when(userProvider.getUserById(fakeNewUserId, realmModel)).thenReturn(newUser);

        when(idpAdminRole.getName()).thenReturn(IDP_ADMIN_ROLE);

        when(event.getRealmId()).thenReturn(FAKE_REALM_ID);
        when(event.getType()).thenReturn(EventType.REGISTER);
        when(event.getUserId()).thenReturn(fakeNewUserId);

        // Given the spy SendEmailEventListenerProvider with mock variables
        SendEmailEventListenerProvider provider = new SendEmailEventListenerProvider(session);
        SendEmailEventListenerProvider spyProvider = spy(provider);
        when(spyProvider.getEmailSenderProvider()).thenReturn(emailSenderProvider);
        when(spyProvider.getRealm()).thenReturn(realmModel);
        when(spyProvider.getIdpAdminRoleModel()).thenReturn(idpAdminRole);

        // When spy provider call sendNotificationEmailForIDPAdmins method
        spyProvider.sendNotificationEmailForIDPAdmins(newUser);

        UserModel firstIdpAdmin = listUsers.get(0);
        UserModel secondIdpAdmin = listUsers.get(3);

        String firstExpectedHtml = "<p>Hello,</p>";
        firstExpectedHtml += "<p><b>Toto Tata</b> has created a new account on " + APPLICATION_NAME + " PCC Platform.</p>";
        firstExpectedHtml += "Please validate this user";

        String secondExpectedHtml = "<p>Hello,</p>";
        secondExpectedHtml += "<p><b>Toto Tata</b> has created a new account on " + APPLICATION_NAME + " PCC Platform.</p>";
        secondExpectedHtml += "Please validate this user";

        // Expect the emailSenderProvider is called twice with the following arguments
        try {
            verify(emailSenderProvider)
                    .send(fakeSmtpConfig, firstIdpAdmin, "New User Created!", "", firstExpectedHtml);
            verify(emailSenderProvider)
                    .send(fakeSmtpConfig, secondIdpAdmin, "New User Created!", "", secondExpectedHtml);
            verifyNoMoreInteractions(emailSenderProvider);
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendNewAccountCreatedConfirmationEmailToUser() {
        // Mock all objects required for the function
        DefaultEmailSenderProvider emailSenderProvider = mock(DefaultEmailSenderProvider.class);
        KeycloakContext keycloakContext = mock(KeycloakContext.class);
        Map<String,String> fakeSmtpConfig = new HashMap<>();

        UserModel newUser = new TestUserModel(fakeNewUserId, "Toto", "Tata");

        when(session.realms()).thenReturn(realmProvider);
        when(session.getContext()).thenReturn(keycloakContext);
        when(session.users()).thenReturn(userProvider);

        when(keycloakContext.getRealm()).thenReturn(realmModel);

        when(realmModel.getSmtpConfig()).thenReturn(fakeSmtpConfig);

        when(userProvider.getUserById(fakeNewUserId, realmModel)).thenReturn(newUser);

        when(event.getRealmId()).thenReturn(FAKE_REALM_ID);
        when(event.getType()).thenReturn(EventType.REGISTER);
        when(event.getUserId()).thenReturn(fakeNewUserId);

        // Given the spy SendEmailEventListenerProvider with mock variables
        SendEmailEventListenerProvider provider = new SendEmailEventListenerProvider(session);
        SendEmailEventListenerProvider spyProvider = spy(provider);
        when(spyProvider.getEmailSenderProvider()).thenReturn(emailSenderProvider);
        when(spyProvider.getRealm()).thenReturn(realmModel);

        testSendEmailToNewUser(newUser, spyProvider, emailSenderProvider, fakeSmtpConfig, "normal");
        testSendEmailToNewUser(newUser, spyProvider, emailSenderProvider, fakeSmtpConfig, "withoutFirstName");
        testSendEmailToNewUser(newUser, spyProvider, emailSenderProvider, fakeSmtpConfig, "withoutLastName");
        testSendEmailToNewUser(newUser, spyProvider, emailSenderProvider, fakeSmtpConfig, "withoutAnyNames");

    }

    /**
     * Call the sendNewAccountCreatedConfirmationEmailToUser and check if the emailSenderProvider is called with the correct arguments
     *
     * @param newUser {Keycloak.UserModel}  - the new user who is just created
     * @param spyProvider {SendEmailEventListenerProvider}  - the listener that perform the action sendNewAccountCreatedConfirmationEmailToUser
     * @param emailSenderProvider {Keycloak.DefaultEmailSenderProvider} - the email sender which will send the email
     * @param fakeSmtpConfig {Map<String, String>}  - the config of smtp
     * @param scenario {String}   - the scenario that we want to test
     */
    private void testSendEmailToNewUser(
        UserModel newUser,
        SendEmailEventListenerProvider spyProvider,
        DefaultEmailSenderProvider emailSenderProvider,
        Map<String, String> fakeSmtpConfig,
        String scenario
    ) {
        String expectedHtml = "";

        switch (scenario) {
            case "normal":
                newUser.setFirstName("Toto");
                newUser.setLastName("Tata");
                expectedHtml = "<p>Welcome<b> Toto Tata</b> to <b>" + APPLICATION_NAME + " PCC Platform</b>,</p>";
                expectedHtml += "<p>Your account has been successfully created. ";
                expectedHtml += "You will receive a notification email when your <b>" + APPLICATION_NAME + " administrator</b> grants you access.</p>";
                break;
            case "withoutFirstName":
                newUser.setFirstName(null);
                newUser.setLastName("Tata");
                expectedHtml = "<p>Welcome<b> Tata</b> to <b>" + APPLICATION_NAME + " PCC Platform</b>,</p>";
                expectedHtml += "<p>Your account has been successfully created. ";
                expectedHtml += "You will receive a notification email when your <b>" + APPLICATION_NAME + " administrator</b> grants you access.</p>";
                break;
            case "withoutLastName":
                newUser.setFirstName("Toto");
                newUser.setLastName(null);
                expectedHtml = "<p>Welcome<b> Toto</b> to <b>" + APPLICATION_NAME + " PCC Platform</b>,</p>";
                expectedHtml += "<p>Your account has been successfully created. ";
                expectedHtml += "You will receive a notification email when your <b>" + APPLICATION_NAME + " administrator</b> grants you access.</p>";
                break;
            case "withoutAnyNames":
                newUser.setFirstName(null);
                newUser.setLastName(null);
                expectedHtml = "<p>Welcome<b></b> to <b>" + APPLICATION_NAME + " PCC Platform</b>,</p>";
                expectedHtml += "<p>Your account has been successfully created. ";
                expectedHtml += "You will receive a notification email when your <b>" + APPLICATION_NAME + " administrator</b> grants you access.</p>";
                break;
            default:
                break;
        }

        // When call sendNewAccountCreatedConfirmationEmailToUser
        spyProvider.sendNewAccountCreatedConfirmationEmailToUser(newUser);

        // Expect the emailSenderProvider is called with the following arguments
        try {
            verify(emailSenderProvider, times(1))
                    .send(fakeSmtpConfig, newUser, "Your account has been created", "", expectedHtml);
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a list of test users
     *
     * @return List<UserModel> the list expected
     */
    private List<UserModel> createTestUsers() {
        List<UserModel> listUsers = new ArrayList<>();

        TestUserModel idpAdminWithEmail = new TestUserModel("first_id","Eric", "Chan", "ericchan@gmail.com");
        idpAdminWithEmail.addRole(IDP_ADMIN_ROLE);

        TestUserModel idpAdminWithoutEmail = new TestUserModel("second_id","David", "Joe");
        idpAdminWithoutEmail.addRole(IDP_ADMIN_ROLE);

        TestUserModel normalUser = new TestUserModel("third_id","Emily", "Page", "emily@gmail.com");

        TestUserModel anotherIdpAdminWithEmail = new TestUserModel("fourth_id","Christ", "Foo", "corentin@gmail.com");
        anotherIdpAdminWithEmail.addRole(IDP_ADMIN_ROLE);

        listUsers.add(idpAdminWithEmail);
        listUsers.add(idpAdminWithoutEmail);
        listUsers.add(normalUser);
        listUsers.add(anotherIdpAdminWithEmail);

        return listUsers;
    }
}
