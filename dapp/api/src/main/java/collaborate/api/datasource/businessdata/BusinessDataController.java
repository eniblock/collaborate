package collaborate.api.datasource.businessdata;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.businessdata.access.AccessRequestService;
import collaborate.api.datasource.businessdata.access.model.AccessRequestDTO;
import collaborate.api.datasource.businessdata.document.ScopeAssetsService;
import collaborate.api.datasource.businessdata.document.model.ScopeAssetsDTO;
import collaborate.api.datasource.businessdata.find.FindBusinessDataService;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.tag.model.job.Job;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

  private final AccessRequestService accessRequestService;
  private final ApiProperties apiProperties;
  private final FindBusinessDataService findBusinessDataService;
  private final ScopeAssetsService scopeAssetsService;
  private final NftDatasourceService nftDatasourceService;

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
  public Job grantAccessRequest(
      @RequestBody @NotEmpty List<@Valid AccessRequestDTO> accessRequestDTOs) {
    return accessRequestService.requestAccess(accessRequestDTOs);
  }

  @GetMapping("asset/{tokenId}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "See all the Business-data assets (documents) of the specified token scope"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_GRANT_ACCESS_REQUEST)
  public ScopeAssetsDTO listAssetDocuments(@PathVariable Integer tokenId)
      throws InterruptedException {
    if (nftDatasourceService.saveGatewayConfigurationByTokenId(tokenId,
        apiProperties.getBusinessDataContractAddress())) {
      // Wait a while to ensure that traefik has loaded the configuration
      Thread.sleep(1000);
    }

    var assets = scopeAssetsService.listScopeAssets(tokenId);
    if (assets.isEmpty()) {
      log.debug("No assets documents for token={}", tokenId);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return assets.get();
  }

  @PostMapping("asset/download")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Download a set of assets"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_READ)
  public void download(
      @RequestBody ScopeAssetsDTO scopeAssets, HttpServletResponse response) throws IOException {
    response.setHeader("Content-Disposition", "attachment; filename=download.zip");
    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    scopeAssetsService.download(scopeAssets, response.getOutputStream());
  }
}
