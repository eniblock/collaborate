package collaborate.api.user.security;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import collaborate.api.config.security.FeignKeycloakConfiguration;
import collaborate.api.user.model.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "keycloak-client", url = "${keycloak.auth-server-url}/realms/${keycloak.realm}/custom-api/users", configuration = FeignKeycloakConfiguration.class)
public interface KeycloakUsersClient {

  @Operation(description = "User details")
  @GetMapping("{id}")
  Optional<UserDTO> findById(@PathVariable("id") UUID id);

  @Operation(description = "Users list")
  @GetMapping(value = "", consumes = APPLICATION_JSON_VALUE)
  UserSearchResponseDTO findByCriteria(@SpringQueryMap UserSearchCriteria search);
}
