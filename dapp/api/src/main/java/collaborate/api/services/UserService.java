package collaborate.api.services;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.errors.UserIdNotFoundException;
import collaborate.api.helper.SetRolesNotificationEmailHelper;
import collaborate.api.services.dto.EditUserDTO;
import collaborate.api.services.dto.UserDTO;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.ws.rs.NotFoundException;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerErrorException;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final Set<String> defaultKeycloakRoles;

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private RealmResource realmResource;

    @Autowired
    private KeycloakService keycloakService;

    @Autowired
    private MailProperties mailProperties;

    @Autowired
    private MailService mailService;

    public static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";
    private static final String EMAIL_SIMPLE_TEMPLATE_NAME = "html/contactEmail.html";


    public UserService() {
        defaultKeycloakRoles = initializeDefaultKeycloakRoles();
    }

    private Set<String> initializeDefaultKeycloakRoles() {
        Set<String> defaultKeycloakRoles = new HashSet<>();

        defaultKeycloakRoles.add("uma_authorization");
        defaultKeycloakRoles.add("offline_access");

        return defaultKeycloakRoles;
    }

    /**
     * Returns a page of all users
     *
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> listUsers(Pageable pageable) {
        return keycloakService.findAll(pageable);
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

    /**
     *
     * @param {UUID} userId  - the user id
     * @return {
     *
     * @throws UserIdNotFoundException
     */
    @Transactional(readOnly = true)
    public UserDTO findOneByUserId(UUID userId) {
        return keycloakService.findOneByIdOrElseThrow(userId);
    }

    /**
     * Modify the user details
     * @param {String} userId  - the id of the user that will be modified
     * @param {EditUserDTO} userDetails  - all of the details that need to be updated
     *
     * @return {UserDTO} the updated user
     */
    public UserDTO modifyUser(String userId, EditUserDTO userDetails) throws UserIdNotFoundException {
        UserResource userResource = realmResource.users().get(userId);
        updateUserRoles(userResource.roles().realmLevel(), userDetails.getRolesNames(), userResource.toRepresentation());
        return keycloakService.findOneByIdOrElseThrow(UUID.fromString(userId));
    }

    /**
     * Update user roles by filtering which roles should be add and which roles should be removed
     * If there is any roles modification, send a notification email for user
     *
     * @param {RoleScopeResource} userRoleScopeResource   - the scope holding user's roles details
     * @param {Set<String} rolesNames                     - the roles' names that user suppose to have
     */
    protected void updateUserRoles(
        RoleScopeResource userRoleScopeResource,
        Set<String> rolesNames,
        UserRepresentation userRepresentation
    ) {
        List<RoleRepresentation> effectiveRoles = userRoleScopeResource.listEffective();

        Set<String> effectiveRolesNames = effectiveRoles.stream()
                .filter(role -> !defaultKeycloakRoles.contains(role.getName()))
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        Set<String> toRemoveRoles = new HashSet<>(effectiveRolesNames);
        toRemoveRoles.removeAll(rolesNames);

        Set<String> toAddRoles = new HashSet<>(rolesNames);
        toAddRoles.removeAll(effectiveRolesNames);

        List<RoleRepresentation> toAddRoleRepresentations = getRolesRepresentations(toAddRoles);
        List<RoleRepresentation> toRemoveRoleRepresentations = getRolesRepresentations(toRemoveRoles);

        userRoleScopeResource.add(toAddRoleRepresentations);
        userRoleScopeResource.remove(toRemoveRoleRepresentations);

        sendNotificationEmail(toAddRoleRepresentations, toRemoveRoleRepresentations, userRepresentation, rolesNames);
    }

    /**
     * Check whether a notification email should be sent to users when roles are changed
     *
     * @param toAddRoleRepresentations {List<RoleRepresentation>}         - list of the roles that should be added
     * @param toRemoveRoleRepresentations {List<RoleRepresentation>}      - list of the roles that should be removed
     * @param userRepresentation {UserRepresentation} userRepresentation  - the user that has the roles change
     * @param rolesNames {Set<String>}                                    - the current roles names that user has
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

        SetRolesNotificationEmailHelper emailHelper = new SetRolesNotificationEmailHelper(apiProperties.getIdpAdminRole());

        try {
            log.info("Sending email about updating user roles");
            mailService.sendMail(
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
     * @param {Set<String>} rolesNames  - the roles names that need to be converted to RoleRepresentation
     *
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
                .filter(roleRepresentation -> roleRepresentation != null)
                .collect(Collectors.toList());
    }
}
