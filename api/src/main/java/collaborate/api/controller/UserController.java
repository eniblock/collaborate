package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.errors.UserIdNotFoundException;
import collaborate.api.services.UserService;
import collaborate.api.services.dto.EditUserDTO;
import collaborate.api.services.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.ws.rs.NotFoundException;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    private final String IDP_ADMIN_AUTHORIZATION = "hasRole('service_identity_provider_administrator')";

    public UserController(UserService service) {
        this.userService = service;
    }

    @GetMapping("/users")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    public Page<UserDTO> listUsersByPage(Pageable pageable) {
        return userService.listUsers(pageable);
    }

    @PostMapping("/users/{id}")
    @Operation(
            description = "Set the roles of the user.",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(IDP_ADMIN_AUTHORIZATION)
    public void setRole(
            @PathVariable(value="id") String userId,
            @Valid @RequestBody EditUserDTO user
    ) {
        userService.modifyUsers(userId, user);
    }

}
