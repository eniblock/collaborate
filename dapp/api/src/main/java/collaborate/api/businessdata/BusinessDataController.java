package collaborate.api.businessdata;

import collaborate.api.businessdata.find.FindBusinessDataService;
import collaborate.api.config.OpenApiConfig;
import collaborate.api.nft.model.AssetDetailsDTO;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
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
public class BusinessDataController {

  private final FindBusinessDataService findBusinessDataService;

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

  @PostMapping("grant-access")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Make a grant access request for the given tokens"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_GRANT_ACCESS_REQUEST)
  @Valid
  public void grantAccessRequest(@RequestBody List<Integer> tokenIds) {
    if (CollectionUtils.isEmpty(tokenIds) || tokenIds.stream().allMatch(Objects::isNull)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
  }
}
