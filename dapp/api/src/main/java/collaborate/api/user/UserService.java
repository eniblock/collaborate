package collaborate.api.user;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;

import collaborate.api.cache.CacheConfig.CacheNames;
import collaborate.api.cache.CacheService;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.mail.EMailService;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.model.RolesDTO;
import collaborate.api.user.model.TransferDTO;
import collaborate.api.user.model.TransferTransactionDTO;
import collaborate.api.user.model.UserDTO;
import collaborate.api.user.security.KeycloakUserService;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerErrorException;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

  private static final Set<String> DEFAULT_KEYCLOAK_ROLES = Set.of(
      "uma_authorization",
      "offline_access"
  );
  public static final String ORGANIZATION_USER_ID = "admin";

  private final ApiProperties apiProperties;
  private final CacheService cacheService;
  private final KeycloakUserService keycloakUserService;
  private final MailProperties mailProperties;
  private final EMailService EMailService;
  private final RealmResource realmResource;
  private final TagUserDAO tagUserDAO;

  public static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";
  private static final String EMAIL_SIMPLE_TEMPLATE_NAME = "html/contactEmail.html";

  @Transactional(readOnly = true)
  public Page<UserDTO> listUsers(Pageable pageable) {
    return keycloakUserService.findAll(pageable);
  }

  @Retryable(
      value = {IOException.class, ServerErrorException.class},
      maxAttempts = 120,
      backoff = @Backoff(delay = 1000)
  )
  public void retriableKeycloak() {
    log.info("Wait for keycloak");
    realmResource.users().list(0, 1);
  }

  @Transactional(readOnly = true)
  public UserDTO findOneByUserId(UUID userId) {
    return keycloakUserService.findOneByIdOrElseThrow(userId);
  }

  /**
   * Modify the user details
   *
   * @param userId   - the id of the user that will be modified
   * @param rolesDTO - all of the details that need to be updated
   * @return {UserDTO} the updated user
   */
  public UserDTO updateRoles(String userId, RolesDTO rolesDTO) {
    UserResource userResource = realmResource.users().get(userId);
    updateUserRoles(userResource.roles().realmLevel(), rolesDTO.getRolesNames(),
        userResource.toRepresentation());
    return keycloakUserService.findOneByIdOrElseThrow(UUID.fromString(userId));
  }

  /**
   * Update user roles by filtering which roles should be add and which roles should be removed If
   * there is any roles modification, send a notification email for user
   *
   * @param userRoleScopeResource - the scope holding user's roles details
   * @param rolesNames            - the roles' names that user suppose to have
   */
  protected void updateUserRoles(
      RoleScopeResource userRoleScopeResource,
      Set<String> rolesNames,
      UserRepresentation userRepresentation
  ) {
    List<RoleRepresentation> effectiveRoles = userRoleScopeResource.listEffective();

    Set<String> effectiveRolesNames = effectiveRoles.stream()
        .map(RoleRepresentation::getName)
        .filter(name -> !DEFAULT_KEYCLOAK_ROLES.contains(name))
        .collect(Collectors.toSet());

    Set<String> toRemoveRoles = new HashSet<>(effectiveRolesNames);
    toRemoveRoles.removeAll(rolesNames);

    Set<String> toAddRoles = new HashSet<>(rolesNames);
    toAddRoles.removeAll(effectiveRolesNames);

    List<RoleRepresentation> toAddRoleRepresentations = getRolesRepresentations(toAddRoles);
    List<RoleRepresentation> toRemoveRoleRepresentations = getRolesRepresentations(toRemoveRoles);

    userRoleScopeResource.add(toAddRoleRepresentations);
    userRoleScopeResource.remove(toRemoveRoleRepresentations);

    sendNotificationEmail(toAddRoleRepresentations, toRemoveRoleRepresentations, userRepresentation,
        rolesNames);
  }

  /**
   * Check whether a notification email should be sent to users when roles are changed
   *
   * @param toAddRoleRepresentations    {List<RoleRepresentation>}         - list of the roles that
   *                                    should be added
   * @param toRemoveRoleRepresentations {List<RoleRepresentation>}      - list of the roles that
   *                                    should be removed
   * @param userRepresentation          {UserRepresentation} userRepresentation  - the user that has
   *                                    the roles change
   * @param rolesNames                  {Set<String>}                                    - the
   *                                    current roles names that user has
   */
  protected void sendNotificationEmail(
      List<RoleRepresentation> toAddRoleRepresentations,
      List<RoleRepresentation> toRemoveRoleRepresentations,
      UserRepresentation userRepresentation,
      Set<String> rolesNames
  ) {
    if ((toAddRoleRepresentations.isEmpty() && toRemoveRoleRepresentations.isEmpty())
        || userRepresentation.getEmail() == null) {
      return;
    }

    SetRolesNotificationEmailHelper emailHelper = new SetRolesNotificationEmailHelper(
        apiProperties.getIdpAdminRole()
    );

    try {
      log.info("Sending email about updating user roles");
      EMailService.sendMail(
          emailHelper.buildRolesSetNotificationEmail(
              mailProperties.getProperties().get("addressFrom"),
              userRepresentation.getEmail(),
              userRepresentation.getFirstName(),
              userRepresentation.getLastName(),
              rolesNames.toArray(new String[0]),
              apiProperties.getPlatform()
          ),
          EMAIL_TEMPLATE_ENCODING,
          EMAIL_SIMPLE_TEMPLATE_NAME
      );
    } catch (MessagingException e) {
      log.error(e.getMessage());
    }
  }

  /**
   * Convert a set of roles names to a list of RoleRepresentation
   *
   * @param rolesNames - the roles names that need to be converted to RoleRepresentation
   * @return {List<RoleRepresentation>} return the corresponding list of RoleRepresentation
   */
  protected List<RoleRepresentation> getRolesRepresentations(Set<String> rolesNames) {
    RolesResource rolesResource = realmResource.roles();

    return rolesNames.stream()
        .map(roleName -> {
          try {
            RoleResource roleResource = rolesResource.get(roleName);
            return roleResource.toRepresentation();
          } catch (Exception e) {
            if (e instanceof NotFoundException) {
              log.info("Could not find the role with name: " + roleName);
            }
            log.info("There is an error while retrieving the role: " + e.getMessage());
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public Optional<UserWalletDTO> findOneByWalletAddress(String walletAddress) {
    return tagUserDAO.findOneByWalletAddress(walletAddress);
  }

  public UserWalletDTO getByWalletAddress(String userAccountAddress) {
    return findOneByWalletAddress(userAccountAddress)
        .orElseGet(() -> {
          log.warn("No Tag user found for account={}", userAccountAddress);
          return UserWalletDTO.builder().address(userAccountAddress).build();
        });
  }

  /**
   * @deprecated Use {@link #createUser(String)} instead
   */
  @Deprecated(since = "0.5")
  public UserWalletDTO createActiveUser(String userId) {
    return tagUserDAO.createActiveUser(userId).orElseThrow(
        () -> {
          log.error("Can't create userId={} wallet", userId);
          throw new ResponseStatusException(
              BAD_GATEWAY, "Can't create userId=" + userId);
        }
    );
  }

  public UserWalletDTO createUser(String userId) {
    return tagUserDAO.createUser(userId).orElseThrow(
        () -> {
          log.error("Can't create userId={} wallet", userId);
          throw new ResponseStatusException(
              BAD_GATEWAY, "Can't create userId=" + userId);
        }
    );
  }

  public UserWalletDTO findByEmailOrThrow(String email) {
    return tagUserDAO
        .findOneByUserEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, (
            format("No user found for userEmail=%s", email)
        )));
  }

  public String findWalletAddressByEmailOrThrow(String email) {
    return findByEmailOrThrow(email).getAddress();
  }

  public UserWalletDTO getAdminUser() {
    return tagUserDAO
        .findOneByUserId(ORGANIZATION_USER_ID)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, (
            "No admin user"
        )));
  }

  public void enable(String userId, boolean enabled) {
    var userRepresentation = new UserRepresentation();
    userRepresentation.setEnabled(enabled);
    realmResource.users().get(userId)
        .update(userRepresentation);
  }

  public void ensureAdminWalletExists() {
    var adminIsMissing = tagUserDAO.findOneByUserId(ORGANIZATION_USER_ID)
        .map(UserWalletDTO::getAddress)
        .filter(StringUtils::isNotBlank)
        .isEmpty();
    if (adminIsMissing) {
      log.info("No wallet found for the organization, creating one...");
      tagUserDAO.createUser(ORGANIZATION_USER_ID);
      cacheService.clearOrThrow(CacheNames.ORGANIZATION);
    }
  }

  /**
   * @param mutez 1 XTZ = 10^6 mutez
   */
  public void transferMutez(String fromUserId, String recipientAddress, int mutez) {
    tagUserDAO.transferMutez(
        fromUserId,
        new TransferDTO(
            List.of(new TransferTransactionDTO(recipientAddress, mutez)),
            fromUserId
        )
    );
  }
}
