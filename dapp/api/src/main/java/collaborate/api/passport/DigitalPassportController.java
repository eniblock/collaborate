package collaborate.api.passport;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.passport.create.CreatePassportDTO;
import collaborate.api.passport.find.DigitalPassportDTO;
import collaborate.api.tag.model.job.Job;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "digital-passport", description = "the Digital-passport API")
@RequestMapping("/api/v1/digital-passport")
public class DigitalPassportController {

  private final PassportService passportService;

  @PostMapping("/{contract-id}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Consent a digital passport in the Smart-Contract for the connected asset owner")
  @PreAuthorize(HasRoles.ASSET_OWNER)
  public Job consent(@PathVariable(value = "contract-id") Integer contractId) {
    return passportService.consent(contractId);
  }

  @PostMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description =
          "Create a multi-signature entry in the Smart-Contract:"
              + "used as a \"pending\" digital-passport<br>"
              + "NB: The current organization signature is automatically added",
      tags = {"multi-signature"})
  @PreAuthorize(HasRoles.SERVICE_PROVIDER)
  public Job create(@RequestBody @Valid CreatePassportDTO createPassportDTO) {
    return passportService.create(createPassportDTO);
  }

  @GetMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the list of existing digital-passports for the current user"
  )
  @PreAuthorize(HasRoles.ASSET_OWNER_OR_SERVICE_PROVIDER)
  public Collection<DigitalPassportDTO> list() {
    return passportService.getByConnectedUser();
  }

}
