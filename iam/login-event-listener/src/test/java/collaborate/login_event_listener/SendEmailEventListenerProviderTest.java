package collaborate.login_event_listener;

import collaborate.login_event_listner.SendEmailEventListenerProvider;
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

    private String FAKE_REALM_ID = "fake_id";
    private String IDP_ADMIN_ROLE = System.getenv("IDP_ADMIN_ROLE");


    @Before
    public void beforeEach() {
        session = mock(KeycloakSession.class);
        realmProvider = mock(RealmProvider.class);
        realmModel = mock(RealmModel.class);
        event = mock(Event.class);
    }

    @After
    public void afterEach() {
        reset(session);
        reset(realmProvider);
        reset(realmModel);
        reset(event);
    }
    @Test
    public void onEventSetRealmAndWhenEventIsNotRegister() {
        when(session.realms()).thenReturn(realmProvider);
        when(event.getRealmId()).thenReturn(FAKE_REALM_ID);
        when(realmProvider.getRealm(FAKE_REALM_ID)).thenReturn(realmModel);
        when(event.getType()).thenReturn(EventType.LOGOUT);

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
        when(session.realms()).thenReturn(realmProvider);
        when(event.getRealmId()).thenReturn(FAKE_REALM_ID);
        when(realmProvider.getRealm(FAKE_REALM_ID)).thenReturn(realmModel);
        when(event.getType()).thenReturn(EventType.REGISTER);

        SendEmailEventListenerProvider provider = new SendEmailEventListenerProvider(session);

        SendEmailEventListenerProvider spyProvider = spy(provider);
        doNothing().when(spyProvider).sendNotificationEmailForIDPAdmins(event);

        // when call onEvent with event type is Register
        spyProvider.onEvent(event);

        // then
        verify(spyProvider, times(1)).sendNotificationEmailForIDPAdmins(event);
    }

    @Test
    public void sendNotificationEmailForIDPAdmins() {
        // Mock all objects required for the function
        DefaultEmailSenderProvider emailSenderProvider = mock(DefaultEmailSenderProvider.class);
        KeycloakContext keycloakContext = mock(KeycloakContext.class);
        UserProvider userProvider = mock(UserProvider.class);
        Map<String,String> fakeSmtpConfig = new HashMap<>();
        RoleModel idpAdminRole = mock(RoleModel.class);

        List<UserModel> listUsers = createTestUsers();

        String fakeNewUserId = "fake_id";

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
        spyProvider.sendNotificationEmailForIDPAdmins(event);

        UserModel firstIdpAdmin = listUsers.get(0);
        UserModel secondIdpAdmin = listUsers.get(3);

        String firstExpectedHtml = "<p>Hello,</p>";
        firstExpectedHtml += "<p><b>Toto Tata</b> has created a new account on " + System.getenv("APPLICATION_NAME") + " PCC Platform.</p>";
        firstExpectedHtml += "Please validate this user";

        String secondExpectedHtml = "<p>Hello,</p>";
        secondExpectedHtml += "<p><b>Toto Tata</b> has created a new account on " + System.getenv("APPLICATION_NAME") + " PCC Platform.</p>";
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
