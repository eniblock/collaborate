package collaborate.api.user;

import static org.assertj.core.api.Assertions.assertThat;
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

import collaborate.api.cache.CacheConfig.CacheNames;
import collaborate.api.cache.CacheService;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.mail.EMailDTO;
import collaborate.api.mail.EMailService;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.security.KeycloakUserService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import javax.mail.MessagingException;
import javax.ws.rs.NotFoundException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.mail.MailProperties;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  public static final String ADMIN_USER_ID = "admin";
  public static final String IDP_ADMIN_ROLE = "service_identity_provider_administrator";
  @InjectMocks
  UserService userService;
  @Mock
  RolesResource rolesResource;
  @Mock
  RealmResource realmResource;
  @Mock
  KeycloakUserService keycloakUserService;
  @Mock
  EMailService EMailService;
  @Mock
  ApiProperties apiProperties;
  @Mock
  MailProperties mailProperties;
  @Mock
  CacheService cacheService;
  @Captor
  ArgumentCaptor<List<RoleRepresentation>> addFunctionArgumentCaptor;
  @Captor
  ArgumentCaptor<List<RoleRepresentation>> removeFunctionArgumentCaptor;

  @Mock
  TagUserDAO tagUserDAO;

  @AfterEach
  public void afterEach() {
    reset(rolesResource);
    reset(realmResource);
    reset(keycloakUserService);
  }

  @Test
  void testGetRolesRepresentations() {
    // GIVEN
    Set<String> fakeRolesNames = new HashSet<>();
    String fakeRole1 = "role_1";
    String fakeRole2 = "role_2";
    fakeRolesNames.add("role_1");
    fakeRolesNames.add("role_2");

    RoleResource fakeRoleResource = mock(RoleResource.class);
    RoleRepresentation fakeRoleRepresentation = mock(RoleRepresentation.class);

    when(rolesResource.get(fakeRole1)).thenReturn(fakeRoleResource);
    when(rolesResource.get(fakeRole2)).thenThrow(new NotFoundException());
    when(fakeRoleResource.toRepresentation()).thenReturn(fakeRoleRepresentation);

    when(realmResource.roles()).thenReturn(rolesResource);
    // WHEN
    List<RoleRepresentation> result = userService.getRolesRepresentations(fakeRolesNames);
    // THEN
    assertThat(result).hasSize(1);
    assertEquals(result.get(0), fakeRoleRepresentation);
  }

  @Test
  void testSetUserRoles() {
    // GIVEN
    UserService userService = spy(this.userService);

    String role1 = "role_1";
    String role2 = "role_2";
    String role3 = "role_3";

    RoleRepresentation roleRepresentationToAdd = new RoleRepresentation();
    roleRepresentationToAdd.setName(role1);
    var roleResourceToAdd = mockRoleResourceRepresentation(roleRepresentationToAdd);
    when(rolesResource.get(role1)).thenReturn(roleResourceToAdd);

    RoleRepresentation roleRepresentationToRemove = new RoleRepresentation();
    roleRepresentationToRemove.setName(role3);
    var roleResourceToRemove = mockRoleResourceRepresentation(roleRepresentationToRemove);
    when(rolesResource.get(role3)).thenReturn(roleResourceToRemove);

    RoleRepresentation roleRepresentation = new RoleRepresentation();
    roleRepresentation.setName(role2);

    when(realmResource.roles()).thenReturn(rolesResource);

    RoleScopeResource roleScopeResource = mock(RoleScopeResource.class);
    when(roleScopeResource.listEffective())
        .thenReturn(List.of(
            roleRepresentationToRemove,
            roleRepresentation)
        );

    doNothing().when(roleScopeResource).add(anyList());
    doNothing().when(roleScopeResource).remove(anyList());

    doNothing().when(userService).sendNotificationEmail(anyList(), anyList(), any(), anySet());

    // WHEN
    userService.updateUserRoles(roleScopeResource, Set.of(role1, role2),
        mock(UserRepresentation.class));

    // THEN
    List<RoleRepresentation> addRoles = List.of(roleRepresentationToAdd);
    verify(roleScopeResource, times(1)).add(addFunctionArgumentCaptor.capture());
    assertEquals(1, addFunctionArgumentCaptor.getAllValues().size());
    assertEquals(addRoles, addFunctionArgumentCaptor.getAllValues().get(0));

    List<RoleRepresentation> removeRoles = List.of(roleRepresentationToRemove);
    verify(roleScopeResource, times(1)).remove(removeFunctionArgumentCaptor.capture());
    assertEquals(1, removeFunctionArgumentCaptor.getAllValues().size());
    assertEquals(removeRoles, removeFunctionArgumentCaptor.getAllValues().get(0));
  }

  @NotNull
  private RoleResource mockRoleResourceRepresentation(
      RoleRepresentation roleRepresentation) {
    RoleResource roleResource = mock(RoleResource.class);
    when(roleResource.toRepresentation()).thenReturn(roleRepresentation);
    return roleResource;
  }

  @Test
  void testSendNotificationEmail() {
    UserRepresentation mockUserRepresentation = mock(UserRepresentation.class);

    List<RoleRepresentation> toAdd = new ArrayList<>();
    List<RoleRepresentation> toRemove = new ArrayList<>();

    Set<String> rolesNames = new HashSet<>();

    when(apiProperties.getIdpAdminRole()).thenReturn(IDP_ADMIN_ROLE);
    when(mailProperties.getProperties()).thenReturn(Map.of("addressFrom", "from@gmail.com"));

    //WHEN
    try {
      doNothing().when(EMailService).sendMail(any(EMailDTO.class), anyString(), anyString());
      // Both lists does not have any values
      userService.sendNotificationEmail(toAdd, toRemove, mockUserRepresentation, rolesNames);
      verify(EMailService, times(0)).sendMail(any(EMailDTO.class), anyString(), anyString());

      // when one of the list have a value
      when(mockUserRepresentation.getEmail()).thenReturn("user@gmail.com");
      toAdd.add(mock(RoleRepresentation.class));
      userService.sendNotificationEmail(toAdd, toRemove, mockUserRepresentation, rolesNames);
      verify(EMailService, times(1))
          .sendMail(any(EMailDTO.class), eq("UTF-8"), eq("html/contactEmail.html"));

      // when email is null
      when(mockUserRepresentation.getEmail()).thenReturn(null);
      toAdd.add(mock(RoleRepresentation.class));
      userService.sendNotificationEmail(toAdd, toRemove, mockUserRepresentation, rolesNames);
      verifyNoMoreInteractions(EMailService);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }


  @ParameterizedTest
  @MethodSource("ensureAdminWalletExistsParams")
  void ensureAdminWalletExists_shouldCreateUserAndClearOrganizationCache(String walletAddress,
      int expectedInvocationNb) {
    // GIVEN
    when(tagUserDAO.findOneByUserId(ADMIN_USER_ID))
        .thenReturn(Optional.of(UserWalletDTO.builder()
            .address(walletAddress)
            .build()));
    // WHEN
    userService.ensureAdminWalletExists();
    // THEN
    verify(tagUserDAO, times(expectedInvocationNb)).createUser(ADMIN_USER_ID);
    verify(cacheService, times(expectedInvocationNb)).clearOrThrow(CacheNames.ORGANIZATION);
  }

  private static Stream<Arguments> ensureAdminWalletExistsParams() {
    return Stream.of(
        Arguments.of(null, 1),
        Arguments.of("  ", 1),
        Arguments.of("aWalletAddress", 0)
    );
  }

  @Test
  void ensureAdminWalletExists_shouldCreateUser_withAdminWalletNotExists() {
    // GIVEN
    when(tagUserDAO.findOneByUserId(ADMIN_USER_ID))
        .thenReturn(Optional.empty());
    // WHEN
    userService.ensureAdminWalletExists();
    // THEN
    verify(tagUserDAO, times(1)).createUser(ADMIN_USER_ID);
    verify(cacheService, times(1)).clearOrThrow(CacheNames.ORGANIZATION);
  }

}
