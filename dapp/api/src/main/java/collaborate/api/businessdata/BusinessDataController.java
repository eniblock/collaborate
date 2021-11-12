package collaborate.api.businessdata;

import collaborate.api.businessdata.access.AccessRequestService;
import collaborate.api.businessdata.document.AssetIdConstraint;
import collaborate.api.businessdata.document.DocumentService;
import collaborate.api.businessdata.document.model.ScopeAssetsDTO;
import collaborate.api.businessdata.find.FindBusinessDataService;
import collaborate.api.config.OpenApiConfig;
import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.tag.model.job.Job;
import collaborate.api.user.security.Authorizations.HasRoles;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "business-data", description = "the Business-data API")
@RequestMapping("/api/v1/business-data")
@Validated
public class BusinessDataController {

  private final DocumentService documentService;
  private final FindBusinessDataService findBusinessDataService;
  private final AccessRequestService accessRequestService;

  @GetMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the business data catalog (list of scopes)"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_READ)
  public Collection<AssetDetailsDTO> list() {
    var result = findBusinessDataService.getAll();
    if (CollectionUtils.isEmpty(result)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return result;
  }

  @PostMapping("access-request")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Make a grant access request for the given tokens"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_GRANT_ACCESS_REQUEST)
  public Job grantAccessRequest(@RequestBody @NotEmpty List<@Valid AssetDetailsDTO> assetDetails) {
    return accessRequestService.requestAccess(assetDetails);
  }

  @GetMapping("asset/{assetId}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "See all the Business-data assets (documents) of one scope"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_GRANT_ACCESS_REQUEST)
  public ScopeAssetsDTO listAssetDocuments(@PathVariable @AssetIdConstraint String assetId) {
    return null;
  }

  @GetMapping("asset/{datasourceId}/{scope}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "See all the Business-data assets (documents) of one scope"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_GRANT_ACCESS_REQUEST)
  public ResponseEntity<JsonNode> listAssetDocuments(
      @PathVariable String datasourceId, @PathVariable String scope) {
    return documentService.listAssetDocuments(datasourceId, scope);
  }
}
