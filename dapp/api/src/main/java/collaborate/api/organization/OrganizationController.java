package collaborate.api.organization;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "organization", description = "the Organization API")
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

  private final OrganizationService organizationService;

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
  @PreAuthorize(HasRoles.SERVICE_PROVIDER)
  public Collection<OrganizationDTO> findAll() {
    return organizationService.getAllOrganizations();
  }

}
