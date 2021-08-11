package collaborate.api.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.services.dto.MailDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.mail.MessagingException;
import javax.ws.rs.NotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.mail.MailProperties;


@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserService userService;

    @Mock
    RolesResource mockRolesResource;

    @Mock
    RealmResource mockRealmResource;

    @Mock
    KeycloakService mockKeycloakService;

    @Mock
    MailService mockMailService;

    @Mock
    ApiProperties apiProperties;

    @Mock
    MailProperties mailProperties;

    private String IDP_ADMIN_ROLE = "service_identity_provider_administrator";
    private String FAKE_ADRESS_FROM = "from@gmail.com";

    @Before
    public void beforeEach() {
        Map<String,String> fakeProperties = new HashMap<>();
        fakeProperties.put("addressFrom", FAKE_ADRESS_FROM);

        when(mockRealmResource.roles()).thenReturn(mockRolesResource);
        when(apiProperties.getIdpAdminRole()).thenReturn(IDP_ADMIN_ROLE);
        when(mailProperties.getProperties()).thenReturn(fakeProperties);
    }

    @After
    public void afterEach() {
        reset(mockRolesResource);
        reset(mockRealmResource);
        reset(mockKeycloakService);
    }

    @Test
    public void testGetRolesRepresentations() {
        Set<String> fakeRolesNames = new HashSet<>();
        String fakeRole1 = "role_1";
        String fakeRole2 = "role_2";
        fakeRolesNames.add("role_1");
        fakeRolesNames.add("role_2");

        RoleResource fakeRoleResource = mock(RoleResource.class);
        RoleRepresentation fakeRoleRepresentation = mock(RoleRepresentation.class);

        when(mockRolesResource.get(fakeRole1)).thenReturn(fakeRoleResource);
        when(mockRolesResource.get(fakeRole2)).thenThrow(new NotFoundException());
        when(fakeRoleResource.toRepresentation()).thenReturn(fakeRoleRepresentation);

        List<RoleRepresentation> result = userService.getRolesRepresentations(fakeRolesNames);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), fakeRoleRepresentation);
    }

    @Test
    public void testSetUserRoles() {
        UserService spyUserService = spy(userService);

        // Set up a set of roles which are going to be updated for user
        Set<String> fakeRolesNames = new HashSet<>();
        String fakeRole1 = "role_1";
        String fakeRole2 = "role_2";
        String fakeRole3 = "role_3";
        fakeRolesNames.add(fakeRole1);
        fakeRolesNames.add(fakeRole2);

        //Set up all the RoleRepresentation and RoleResource needed for the test
        RoleResource fakeRoleResourceToAdd = mock(RoleResource.class);
        RoleRepresentation roleRepresentationToAdd = new RoleRepresentation();
        roleRepresentationToAdd.setName(fakeRole1);

        RoleResource fakeRoleResourceToRemove = mock(RoleResource.class);
        RoleRepresentation roleRepresentationToRemove = new RoleRepresentation();
        roleRepresentationToRemove.setName(fakeRole3);

        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(fakeRole2);

        RoleScopeResource mockRoleScopeResource = mock(RoleScopeResource.class);
        List<RoleRepresentation> effectiveRoles = new ArrayList<>();
        effectiveRoles.add(roleRepresentationToRemove);
        effectiveRoles.add(roleRepresentation);

        //Set up UserRepresentation
        UserRepresentation userRepresentation = mock(UserRepresentation.class);

        // GIVEN
        when(mockRolesResource.get(fakeRole1)).thenReturn(fakeRoleResourceToAdd);
        when(mockRolesResource.get(fakeRole3)).thenReturn(fakeRoleResourceToRemove);
        when(fakeRoleResourceToAdd.toRepresentation()).thenReturn(roleRepresentationToAdd);
        when(fakeRoleResourceToRemove.toRepresentation()).thenReturn(roleRepresentationToRemove);
        when(mockRoleScopeResource.listEffective()).thenReturn(effectiveRoles);
        doNothing().when(mockRoleScopeResource).add(anyList());
        doNothing().when(mockRoleScopeResource).remove(anyList());
        // Do not check the sending email process
        doNothing().when(spyUserService).sendNotificationEmail(anyList(), anyList(), any(), anySet());

        // WHEN
        spyUserService.updateUserRoles(mockRoleScopeResource, fakeRolesNames, userRepresentation);

        ArgumentCaptor<List<RoleRepresentation>> addFunctionArgumentCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<RoleRepresentation>> removeFunctionArgumentCaptor = ArgumentCaptor.forClass(List.class);

        verify(mockRoleScopeResource, times(1)).add(addFunctionArgumentCaptor.capture());
        verify(mockRoleScopeResource, times(1)).remove(removeFunctionArgumentCaptor.capture());

        List<RoleRepresentation> addRoles = new ArrayList<>();
        addRoles.add(roleRepresentationToAdd);

        List<RoleRepresentation> removeRoles = new ArrayList<>();
        removeRoles.add(roleRepresentationToRemove);

        // THEN
        assertEquals(1, addFunctionArgumentCaptor.getAllValues().size());
        assertEquals(addRoles, addFunctionArgumentCaptor.getAllValues().get(0));

        assertEquals(1, removeFunctionArgumentCaptor.getAllValues().size());
        assertEquals(removeRoles, removeFunctionArgumentCaptor.getAllValues().get(0));
    }

    @Test
    public void testSendNotificationEmail() {
        UserRepresentation mockUserRepresentation = mock(UserRepresentation.class);

        List<RoleRepresentation> toAdd = new ArrayList<>();
        List<RoleRepresentation> toRemove = new ArrayList<>();

        Set<String> rolesNames = new HashSet<>();

        //WHEN
        try {
            doNothing().when(mockMailService).sendMail(any(MailDTO.class), anyString(), anyString());
            // Both lists does not have any values
            userService.sendNotificationEmail(toAdd, toRemove, mockUserRepresentation, rolesNames);
            verify(mockMailService, times(0)).sendMail(any(MailDTO.class), anyString(), anyString());

            // when one of the list have a value
            when(mockUserRepresentation.getEmail()).thenReturn("user@gmail.com");
            toAdd.add(mock(RoleRepresentation.class));
            userService.sendNotificationEmail(toAdd, toRemove, mockUserRepresentation, rolesNames);
            verify(mockMailService, times(1))
                    .sendMail(any(MailDTO.class), eq("UTF-8"), eq("html/contactEmail.html"));

            // when email is null
            when(mockUserRepresentation.getEmail()).thenReturn(null);
            toAdd.add(mock(RoleRepresentation.class));
            userService.sendNotificationEmail(toAdd, toRemove, mockUserRepresentation, rolesNames);
            verifyNoMoreInteractions(mockMailService);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
