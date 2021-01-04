package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.errors.UserIdNotFoundException;
import collaborate.api.services.UserService;
import collaborate.api.services.dto.EditUserDTO;
import collaborate.api.services.dto.UserDTO;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

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
    @PreAuthorize(IDP_ADMIN_AUTHORIZATION)
    public Page<UserDTO> listUsersByPage(Pageable pageable) {
        return userService.listUsers(pageable);
    }

    @GetMapping("/users/{id}")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(IDP_ADMIN_AUTHORIZATION)
    public UserDTO getUserDetails(@PathVariable(value="id") UUID userId) throws UserIdNotFoundException {
        try {
            return userService.findOneByUserId(userId);
        } catch (Exception ex) {
            if(ex instanceof FeignException.NotFound) {
                throw new UserIdNotFoundException(userId);
            }
            throw ex;
        }
    }

    @PostMapping("/users/{id}")
    @Operation(
            description = "Set the roles of the user.",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(IDP_ADMIN_AUTHORIZATION)
    public UserDTO modifyUser(
            @PathVariable(value="id") String userId,
            @Valid @RequestBody EditUserDTO user
    ) throws UserIdNotFoundException {
        return userService.modifyUser(userId, user);
    }

}
