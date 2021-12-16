package collaborate.api.user.providers;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.user.security.Authorizations.HasRoles;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "user", description = "The User API. Used to get detailed information about user, or update user roles")
@RequestMapping("/api/v1/users/identity-provider")
public class IdentityProviderController {

  private final ObjectMapper objectMapper;
  private final IdentityProviderService identityProviderService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      description = "Add a new identity provider making users able to connect to the dApp using it",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.IDENTITY_ADMIN)
  public void createIdentityProvider(
      @RequestPart("file") MultipartFile identityProviderConfigFile) {
    IdentityProviderRepresentation identityConfig = null;
    try {
      identityConfig = objectMapper.readValue(
          identityProviderConfigFile.getBytes(),
          IdentityProviderRepresentation.class);
    } catch (Exception e) {
      log.error("While reading identity provider configuration", e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    identityProviderService.create(identityConfig);
  }

  @GetMapping()
  @Operation(
      description = "Get all the identity providers",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.IDENTITY_ADMIN)
  public List<IdentityProviderRepresentation> listIdentityProviders() {
    return identityProviderService.findAll();
  }

  @DeleteMapping("{providerAlias}")
  @Operation(
      description = "Remove the identity provider for the given alias",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.IDENTITY_ADMIN)
  public void deleteRoleMapper(@PathVariable("providerAlias") String providerAlias) {
    identityProviderService.deleteIdentityProvider(providerAlias);
  }

  @PostMapping(value = "mapper", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      description = "Create a role mapper, used for giving a default role to users coming from a given identity-provider",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.IDENTITY_ADMIN)
  public void createRoleMapper(@RequestPart("file") MultipartFile mapperFile) {
    IdentityProviderMapperRepresentation mapperConfig = null;
    try {
      mapperConfig = objectMapper.readValue(
          mapperFile.getBytes(),
          IdentityProviderMapperRepresentation.class);
    } catch (Exception e) {
      log.error("While reading identity provider mapper configuration", e);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    identityProviderService.createMapper(mapperConfig);
  }

  @GetMapping("{providerAlias}/mapper")
  @Operation(
      description = "Get the role mappers for the given identity-provider alias",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.IDENTITY_ADMIN)
  public List<IdentityProviderMapperRepresentation> listMappers(
      @PathVariable String providerAlias) {

    return identityProviderService.listMapper(providerAlias);
  }

  @DeleteMapping("{providerAlias}/mapper/{mapperId}")
  @Operation(
      description = "Remove",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.IDENTITY_ADMIN)
  public void deleteRoleMapper(@PathVariable("providerAlias") String providerAlias,
      @PathVariable("mapperId") String mapperId) {
    identityProviderService.deleteMapper(providerAlias, mapperId);
  }

}
