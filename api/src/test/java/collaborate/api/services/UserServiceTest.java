package collaborate.api.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

    @Before
    public void beforeEach() {
        when(mockRealmResource.roles()).thenReturn(mockRolesResource);
    }

    @After
    public void afterEach() {
        reset(mockRolesResource);
        reset(mockRealmResource);
        reset(mockKeycloakService);
    }

    @Test
    public void testGetRolesRepresentations() {
        UserService userService = new UserService(mockRealmResource, mockKeycloakService);
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
        UserService userService = new UserService(mockRealmResource, mockKeycloakService);

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

        // GIVEN
        when(mockRolesResource.get(fakeRole1)).thenReturn(fakeRoleResourceToAdd);
        when(mockRolesResource.get(fakeRole3)).thenReturn(fakeRoleResourceToRemove);
        when(fakeRoleResourceToAdd.toRepresentation()).thenReturn(roleRepresentationToAdd);
        when(fakeRoleResourceToRemove.toRepresentation()).thenReturn(roleRepresentationToRemove);
        when(mockRoleScopeResource.listEffective()).thenReturn(effectiveRoles);
        doNothing().when(mockRoleScopeResource).add(anyList());
        doNothing().when(mockRoleScopeResource).remove(anyList());

        // WHEN
        userService.updateUserRoles(mockRoleScopeResource, fakeRolesNames);

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
}
