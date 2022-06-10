package collaborate.api.organization;

import static collaborate.api.cache.CacheConfig.CacheNames.ORGANIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.organization.model.OrganizationDTO;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@Tag(name = "organization", description = "the Organization API")
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

  private final OrganizationService organizationService;
  private final String organizationYellowPageContractAddress;

  @GetMapping()
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get all existing organizations stored in the Smart-Contract",
      tags = {"organization"}
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Organizations has been found",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrganizationDTO.class))))})
  @PreAuthorize(HasRoles.ORGANIZATION_READ)
  public Collection<OrganizationDTO> getAllOrganizations() {
    return organizationService.getAllOrganizations();
  }

  @GetMapping("/current")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the organization who owns the dapp",
      tags = {"organization"}
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Organization has been found",
          content = @Content(schema = @Schema(implementation = OrganizationDTO.class)))})
  @PreAuthorize(HasRoles.ORGANIZATION_READ)
  public OrganizationDTO getCurrentOrganization() {
    return organizationService.getCurrentOrganization();
  }

  @GetMapping("/{walletAddress}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get organization by its wallet address",
      tags = {"organization"}
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Organization has been found",
          content = @Content(schema = @Schema(implementation = OrganizationDTO.class)))})
  @Cacheable(value = ORGANIZATION)
  @PreAuthorize(HasRoles.ORGANIZATION_READ)
  public OrganizationDTO getByAddress(@PathVariable String walletAddress) {
    return organizationService.findOrganizationByPublicKeyHash(walletAddress,
            organizationYellowPageContractAddress)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
  }
}
