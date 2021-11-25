package collaborate.api.datasource.passport;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.datasource.passport.consent.ConsentService;
import collaborate.api.datasource.passport.create.CreateMultisigPassportDTO;
import collaborate.api.datasource.passport.create.CreatePassportService;
import collaborate.api.datasource.passport.find.FindPassportService;
import collaborate.api.datasource.passport.metric.MetricService;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.datasource.passport.model.Metric;
import collaborate.api.tag.model.job.Job;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Collection;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "digital-passport", description = "the Digital-passport API")
@RequestMapping("/api/v1/digital-passport")
public class DigitalPassportController {

  private final ConsentService consentService;
  private final CreatePassportService createPassportService;
  private final FindPassportService findPassportService;
  private final MetricService metricService;

  @PostMapping("/multisig/{contract-id}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Consent a digital passport in the Smart-Contract for the connected asset owner")
  @PreAuthorize(HasRoles.ASSET_OWNER)
  public Job consent(@PathVariable(value = "contract-id") Integer contractId) {
    return consentService.consent(contractId);
  }

  @GetMapping("/multisig/{contract-id}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get pending digital passport details from it multisig contract id")
  @PreAuthorize(HasRoles.PASSPORT_MULTISIG_READ)
  public ResponseEntity<DigitalPassportDetailsDTO> getByMultisigId(
      @PathVariable(value = "contract-id") Integer contractId) {
    return findPassportService.findPassportDetailsFromMultisig(contractId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description =
          "Create a multi-signature entry in the Smart-Contract:"
              + "used as a \"pending\" digital-passport<br>"
              + "NB: The current organization signature is automatically added",
      tags = {"multi-signature"})
  @PreAuthorize(HasRoles.DSP)
  public Job create(@RequestBody @Valid CreateMultisigPassportDTO createMultisigPassportDTO)
      throws IOException {
    return createPassportService.createMultisig(createMultisigPassportDTO);
  }

  @GetMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the list of existing digital-passports for the current user"
  )
  @PreAuthorize(HasRoles.DIGITAL_PASSPORT_READ)
  public Collection<DigitalPassportDetailsDTO> list() {
    return findPassportService.getByConnectedUser();
  }

  @GetMapping("/{tokenId}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get digital passport details from it token id")
  @PreAuthorize(HasRoles.DIGITAL_PASSPORT_READ)
  public ResponseEntity<DigitalPassportDetailsDTO> getByTokenId(@PathVariable Integer tokenId) {
    return findPassportService.findPassportDetailsByTokenId(tokenId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/{tokenId}/metrics")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get metrics events about the given passport identified by its token id")
  @PreAuthorize(HasRoles.DIGITAL_PASSPORT_READ)
  public ResponseEntity<Page<Metric>> getMetrics(@PathVariable Integer tokenId,
      @SortDefault(sort = "scope", direction = Sort.Direction.ASC) Pageable pageable,
      @RequestParam(required = false, defaultValue = "") String query) {
    return metricService.findAll(tokenId, pageable, query)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/count")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the  number of all existing digital-passports whatever the owner"
  )
  @PreAuthorize(HasRoles.ORGANIZATION_READ)
  public long count() {
    return findPassportService.count();
  }

}
