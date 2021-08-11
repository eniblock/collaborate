package collaborate.api.restclient;

import collaborate.api.config.security.FeignKeycloakConfiguration;
import collaborate.api.services.dto.UserDTO;
import collaborate.api.services.dto.UserSearchCriteria;
import collaborate.api.services.dto.UserSearchResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "keycloak-client", url = "${keycloak.auth-server-url}/realms/${keycloak.realm}/custom-api", configuration = FeignKeycloakConfiguration.class)
public interface IKeycloakController {

    @Operation(description = "User details")
    @GetMapping("/users/{id}")
    Optional<UserDTO> findById(@PathVariable("id") UUID id);

    @Operation(description = "Users list")
    @GetMapping(value = "/users", consumes = "application/json")
    UserSearchResponseDTO findByCriteria(@SpringQueryMap UserSearchCriteria search);
}
