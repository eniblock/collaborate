package collaborate.api.services;

import collaborate.api.errors.UserIdNotFoundException;
import collaborate.api.services.dto.EditUserDTO;
import collaborate.api.services.dto.UserDTO;
import org.keycloak.admin.client.resource.*;

import org.keycloak.representations.idm.RoleRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerErrorException;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final RealmResource realmResource;
    private final KeycloakService keycloakService;
    private final Set<String> defaultKeycloakRoles;

    public UserService(RealmResource realmResource, KeycloakService keycloakService) {
        this.realmResource = realmResource;
        this.keycloakService = keycloakService;
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
     * Modify the user details
     * @param {String} userId  - the id of the user that will be modified
     * @param {EditUserDTO} userDetails  - all of the details that need to be updated
     *
     * @return {UserDTO} the updated user
     */
    public UserDTO modifyUser(String userId, EditUserDTO userDetails) throws UserIdNotFoundException {
        UserResource userResource = realmResource.users().get(userId);
        updateUserRoles(userResource.roles().realmLevel(), userDetails.getRolesNames());
        return keycloakService.findOneByIdOrElseThrow(UUID.fromString(userId));
    }

    /**
     * Update user roles by filtering which roles should be add and which roles should be removed
     *
     * @param {RoleScopeResource} userRoleScopeResource   - the scope holding user's roles details
     * @param {Set<String} rolesNames                     - the roles' names that user suppose to have
     */
    protected void updateUserRoles(RoleScopeResource userRoleScopeResource, Set<String> rolesNames) {
        List<RoleRepresentation> effectiveRoles = userRoleScopeResource.listEffective();

        Set<String> effectiveRolesNames = effectiveRoles.stream()
                .filter(role -> !defaultKeycloakRoles.contains(role.getName()))
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        Set<String> toRemoveRoles = new HashSet<>(effectiveRolesNames);
        toRemoveRoles.removeAll(rolesNames);

        Set<String> toAddRoles = new HashSet<>(rolesNames);
        toAddRoles.removeAll(effectiveRolesNames);

        userRoleScopeResource.add(getRolesRepresentations(toAddRoles));
        userRoleScopeResource.remove(getRolesRepresentations(toRemoveRoles));
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
