package collaborate.api.services;

import collaborate.api.config.properties.ApiProperties;
import collaborate.api.config.properties.MailProperties;
import collaborate.api.services.dto.MailDTO;
import collaborate.api.utils.MailUtils;
import collaborate.api.wrapper.TemplateEngineWrapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Mock
    RolesResource mockRolesResource;

    @Mock
    RealmResource mockRealmResource;

    @Mock
    KeycloakService mockKeycloakService;

    @Mock
    JavaMailSender mockJavaMailSender;

    @Mock
    TemplateEngineWrapper mockTemplateEngineWrapper;

    @Mock
    ApiProperties apiProperties;

    @Mock
    MailProperties mailProperties;

    @Mock
    MailProperties.Properties mockMailProperties;

    private String IDP_ADMIN_ROLE = "service_identity_provider_administrator";
    private String FAKE_ADRESS_FROM = "from@gmail.com";

    @Before
    public void beforeEach() {
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);
        when(apiProperties.getIdpAdminRole()).thenReturn(IDP_ADMIN_ROLE);
        when(mailProperties.getProperties()).thenReturn(mockMailProperties);
        when(mockMailProperties.getAddressFrom()).thenReturn(FAKE_ADRESS_FROM);
    }

    @After
    public void afterEach() {
        reset(mockRolesResource);
        reset(mockRealmResource);
        reset(mockKeycloakService);
    }

    @Test
    public void testGetRolesRepresentations() {
        UserService userService = new UserService(
                mockRealmResource,
                mockKeycloakService,
                mockTemplateEngineWrapper,
                mockJavaMailSender,
                apiProperties,
                mailProperties
        );
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
        UserService userService = new UserService(
                mockRealmResource,
                mockKeycloakService,
                mockTemplateEngineWrapper,
                mockJavaMailSender,
                apiProperties,
                mailProperties
        );
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
        UserService userService = new UserService(
                mockRealmResource,
                mockKeycloakService,
                mockTemplateEngineWrapper,
                mockJavaMailSender,
                apiProperties,
                mailProperties
        );

        UserRepresentation mockUserRepresentation = mock(UserRepresentation.class);

        List<RoleRepresentation> toAdd = new ArrayList<>();
        List<RoleRepresentation> toRemove = new ArrayList<>();

        Set<String> rolesNames = new HashSet<>();

        try (MockedStatic<MailUtils> mockedMailUtil = mockStatic(MailUtils.class)) {
            // Both lists does not have any values
            userService.sendNotificationEmail(toAdd, toRemove, mockUserRepresentation, rolesNames);
            mockedMailUtil.verifyNoInteractions();

            // when one of the list have a value
            toAdd.add(mock(RoleRepresentation.class));
            userService.sendNotificationEmail(toAdd, toRemove, mockUserRepresentation, rolesNames);
            mockedMailUtil.verify(() -> MailUtils.sendMail(
                    any(MailDTO.class),
                    eq("UTF-8"),
                    eq("html/contactEmail.html"),
                    eq(mockJavaMailSender),
                    eq(mockTemplateEngineWrapper)
                    )
            );
        }


    }
}
