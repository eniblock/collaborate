package collaborate.api.user;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.user.model.RolesDTO;
import collaborate.api.user.model.UserDTO;
import collaborate.api.user.security.Authorizations.HasRoles;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  @GetMapping()
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(HasRoles.SERVICE_IDP_ADMIN)
  public Page<UserDTO> listUsersByPage(Pageable pageable) {
    return userService.listUsers(pageable);
  }

  @GetMapping("{id}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(HasRoles.SERVICE_IDP_ADMIN)
  public UserDTO getUserDetails(@PathVariable(value = "id") UUID userId)
      throws UserIdNotFoundException {
    try {
      return userService.findOneByUserId(userId);
    } catch (FeignException.NotFound ex) {
      log.error("user not found", ex);
      throw new UserIdNotFoundException(userId);
    } catch (Exception ex) {
      log.error("getUserDetails", ex);
      throw ex;
    }
  }

  @PostMapping("{id}")
  @Operation(
      description = "Set the roles of the user.",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(HasRoles.SERVICE_IDP_ADMIN)
  public UserDTO modifyUser(
      @PathVariable(value = "id") String userId,
      @Valid @RequestBody RolesDTO user
  ) {
    return userService.modifyUser(userId, user);
  }

  @PostMapping("tag/asset-owner")
  @Operation(
      description = "Call TAG create user endpoint and update the current user role to asset_owner",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(HasRoles.PENDING_ASSET_OWNER)
  public Callable<ResponseEntity<UserDTO>> updateAsAssetOwner() {
    return () -> ResponseEntity.ok(userService.updateCurrentUserWithAssetOwnerRole());
  }
}