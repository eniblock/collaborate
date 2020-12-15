package collaborate.api.services;

import collaborate.api.services.dto.UserDTO;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ServerErrorException;

import java.io.IOException;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final RealmResource realmResource;
    private final KeycloakService keycloakService;

    public UserService(RealmResource realmResource, KeycloakService keycloakService) {
        this.realmResource = realmResource;
        this.keycloakService = keycloakService;
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
}
