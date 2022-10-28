package collaborate.api.datasource.serviceconsent;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.datasource.serviceconsent.consent.ConsentServiceConsentService;
import collaborate.api.datasource.serviceconsent.create.CreateMultisigServiceConsentDTO;
import collaborate.api.datasource.serviceconsent.create.CreateServiceConsentService;
import collaborate.api.datasource.serviceconsent.find.FindServiceConsentService;
import collaborate.api.datasource.serviceconsent.metric.MetricDataServiceService;
import collaborate.api.datasource.serviceconsent.model.ServiceConsentDetailsDTO;
import collaborate.api.datasource.serviceconsent.model.Metric;
import collaborate.api.tag.model.job.Job;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
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
@Tag(name = "service-consent", description = "the service data API")
@RequestMapping("/api/v1/service-consent")
public class ServiceConsentController {

  private final ConsentServiceConsentService consentService;
  private final CreateServiceConsentService createServiceConsentService;
  private final FindServiceConsentService findServiceConsentService;
  private final MetricDataServiceService metricService;

  @PostMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description =
          "Create a multi-signature entry in the Smart-Contract:"
              + "used as a \"pending\" service-data<br>"
              + "NB: The current organization signature is automatically added")
  @PreAuthorize(HasRoles.DSP)
  public Job create(@RequestBody @Valid CreateMultisigServiceConsentDTO createMultisigServiceConsentDTO)
      throws IOException {
    return createServiceConsentService.createMultisig(createMultisigServiceConsentDTO);
  }

  @PostMapping("/multisig/{contract-id}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Consent a service data in the Smart-Contract for the connected asset owner")
  @PreAuthorize(HasRoles.ASSET_OWNER)
  public Job consent(@PathVariable(value = "contract-id") Integer contractId) {
    return consentService.consent(contractId);
  }

  @GetMapping("/multisig/{contract-id}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get pending service data details from it multisig contract id")
  @PreAuthorize(HasRoles.SERVICE_CONSENT_MULTISIG_READ)
  public ResponseEntity<ServiceConsentDetailsDTO> getByMultisigId(
      @PathVariable(value = "contract-id") Integer contractId) {
    return findServiceConsentService.findServiceConsentDetailsFromMultisigId(contractId)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the list of existing service-data for the current user"
  )
  @PreAuthorize(HasRoles.SERVICE_CONSENT_READ)
  public Collection<ServiceConsentDetailsDTO> list() {
    return findServiceConsentService.getByConnectedUser();
  }

  @GetMapping("/{tokenId}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get service data details from it token id")
  @PreAuthorize(HasRoles.SERVICE_CONSENT_READ)
  public ResponseEntity<ServiceConsentDetailsDTO> getByTokenId(@PathVariable Integer tokenId) {
    return findServiceConsentService.findServiceConsentDetailsByTokenIdList(List.of(tokenId))
        .stream().findFirst()
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/{tokenId}/metrics")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get metrics events about the given service data identified by its token id")
  @PreAuthorize(HasRoles.SERVICE_CONSENT_READ)
  public ResponseEntity<Page<Metric>> getMetrics(@PathVariable Integer tokenId,
      @SortDefault(sort = "scope", direction = Sort.Direction.ASC) @ParameterObject Pageable pageable,
      @RequestParam(required = false, defaultValue = "") String query) {
    return metricService.findAll(tokenId, pageable, query)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/count")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the  number of all existing service-data whatever the owner"
  )
  @PreAuthorize(HasRoles.ORGANIZATION_READ)
  public long count() {
    return findServiceConsentService.countServiceConsent();
  }


}
