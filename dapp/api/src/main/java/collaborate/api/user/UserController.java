package collaborate.api.user;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.user.connected.ConnectedUserService;
import collaborate.api.user.model.RolesDTO;
import collaborate.api.user.model.UserDTO;
import collaborate.api.user.security.Authorizations.HasRoles;
import collaborate.api.user.security.Authorizations.Roles;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@Tag(name = "user", description = "The User API. Used to get detailed information about user, or update user roles")
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;
  private final ConnectedUserService connectedUserService;

  @GetMapping()
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(HasRoles.IDENTITY_ADMIN)
  public Page<UserDTO> listUsersByPage(
      @ParameterObject @PageableDefault(size = 20, sort = "email") Pageable pageable) {
    return userService.listUsers(pageable);
  }

  @GetMapping("{id}")
  @Operation(
      description = "Get the user associated to the given {id}.",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "User has been found." + Roles.PENDING_ASSET_OWNER
      )
  })
  @PreAuthorize(HasRoles.IDENTITY_ADMIN)
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
      description = "Set the roles of the user associated to the given {id}.",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(HasRoles.IDENTITY_ADMIN)
  public UserDTO updateUserRoles(
      @Parameter(description = "The id of the user to update") @PathVariable(value = "id") String userId,
      @Valid @RequestBody RolesDTO user
  ) {
    return userService.modifyUser(userId, user);
  }

  @PostMapping("tag/asset-owner")
  @Operation(
      description = "Create user wallet and update the current user role to asset_owner.",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "403",
          description = "Current user has not the following role:" + Roles.PENDING_ASSET_OWNER,
          content = @Content),
      @ApiResponse(responseCode = "201",
          description = "Tag user has been create." + Roles.PENDING_ASSET_OWNER,
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = UserDTO.class))
          })
  })
  @PreAuthorize(HasRoles.PENDING_ASSET_OWNER)
  public Callable<ResponseEntity<UserDTO>> updateAsAssetOwner() {
    return () -> ResponseEntity.ok(connectedUserService.updateWithAssetOwnerRole());
  }
}
