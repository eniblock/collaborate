package collaborate.api.organization;

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
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "organization", description = "the Organization API")
@RequestMapping("/api/v1/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping()
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
            description = "Add an organization to the consortium",
            tags = {"organization"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "The organization has been added",
                    content = @Content(schema = @Schema(implementation = OrganizationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The provided data are incorrect")
    })
    @PreAuthorize(HasRoles.BNO)
    public ResponseEntity<OrganizationDTO> addOrganization(@Valid @RequestBody OrganizationDTO organization) {
        var insertedOrganization = organizationService.upsertOrganization(organization);
        return new ResponseEntity<>(insertedOrganization, HttpStatus.CREATED);
    }

    @PostMapping("/withGoldenToken")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
            description = "Add an organization to the consortium, using a Golden Token",
            tags = {"organization"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "The organization has been added",
                    content = @Content(schema = @Schema(implementation = OrganizationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "The provided data are incorrect")
    })
    public ResponseEntity<OrganizationDTO> addOrganizationUsingGoldenToken(@Valid @RequestBody OrganizationDTO organization) {
        var insertedOrganization = organizationService.upsertOrganizationUsingGoldenToken(organization);
        return new ResponseEntity<>(insertedOrganization, HttpStatus.CREATED);
    }

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
    @PreAuthorize(HasRoles.ORGANIZATION_READ)
    public OrganizationDTO getByAddress(@PathVariable String walletAddress) {
        return organizationService.findOrganizationByPublicKeyHash(walletAddress)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

}



