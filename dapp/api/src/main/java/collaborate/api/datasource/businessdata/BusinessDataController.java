package collaborate.api.datasource.businessdata;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.datasource.businessdata.access.RequestAccessService;
import collaborate.api.datasource.businessdata.access.model.AccessRequestDTO;
import collaborate.api.datasource.businessdata.document.AssetsService;
import collaborate.api.datasource.businessdata.document.model.BusinessDataNFTSummary;
import collaborate.api.datasource.businessdata.document.model.ScopeAssetDTO;
import collaborate.api.datasource.businessdata.document.model.ScopeAssetsDTO;
import collaborate.api.datasource.businessdata.find.FindBusinessDataService;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.tag.model.job.Job;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@Slf4j
@Tag(name = "business-data", description = "the Business-data API")
@RequestMapping("/api/v1/business-data")
@Validated
public class BusinessDataController {

  private final RequestAccessService accessRequestService;
  private final String businessDataContractAddress;
  private final FindBusinessDataService findBusinessDataService;
  private final AssetsService assetsService;
  private final NftDatasourceService nftDatasourceService;

  @GetMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the business data catalog (list of scopes)"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_READ)
  public HttpEntity<Page<AssetDetailsDTO>> listAssetDetails(
      Pageable pageable,
      @RequestParam(required = false) Optional<String> query,
      @RequestParam(required = false) Optional<String> ownerAddress
  ) {
    var result = findBusinessDataService.find(pageable, query, ownerAddress);
    if (result.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("access-request")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Make a grant access request for the given tokens"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_GRANT_ACCESS_REQUEST)
  public Job requestAccess(
      @RequestBody @NotEmpty List<@Valid AccessRequestDTO> accessRequestDTOs) {
    return accessRequestService.requestAccess(accessRequestDTOs);
  }

  private void waitForDatasourceConfiguration(Integer tokenId) throws InterruptedException {
    if (nftDatasourceService.saveGatewayConfigurationByTokenId(
        tokenId,
        businessDataContractAddress)
    ) {
      // Wait a while to ensure that traefik has loaded the configuration
      Thread.sleep(1000);
    }
  }

  @GetMapping("asset/{tokenId}/summary")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get some basics informations for the given NFT"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_READ)
  public BusinessDataNFTSummary listAssetDocuments(@PathVariable Integer tokenId)
      throws InterruptedException {
    waitForDatasourceConfiguration(tokenId);

    return assetsService.getSummary(tokenId);
  }

  @GetMapping("asset/{tokenId}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "See all the Business-data assets (documents) of the specified token id"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_READ)
  public Page<ScopeAssetDTO> listAssetDocuments(@PathVariable Integer tokenId, Pageable pageable)
      throws InterruptedException {
    waitForDatasourceConfiguration(tokenId);
    return assetsService.listScopeAssets(tokenId, pageable);
  }

  @GetMapping(value = {"asset/{tokenId}/test-connection", "asset/{tokenId}/fetch/{assetIdOpt}"})
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Try to consume an element of the business data collection"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "The user has access to the asset"),
      @ApiResponse(responseCode = "404", description = "The asset is empty"),
      @ApiResponse(responseCode = "407", description = "Not existing or expired access request token"),
      @ApiResponse(responseCode = "502", description = "Unavailable datasource")}
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_READ)
  public ResponseEntity<String> fetch(@PathVariable Integer tokenId,
      @PathVariable Optional<String> assetIdOpt)
      throws InterruptedException {
    waitForDatasourceConfiguration(tokenId);

    var assetResponse = assetsService.fetch(tokenId, assetIdOpt);
    var statusCode = assetResponse.getStatusCode();
    if (statusCode == HttpStatus.SERVICE_UNAVAILABLE) {
      statusCode = HttpStatus.BAD_GATEWAY;
    }

    var contentType = assetResponse.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
    if (contentType != null) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.put(HttpHeaders.CONTENT_TYPE, List.of(contentType));
      return new ResponseEntity<>(
          assetIdOpt.map(o -> assetResponse.getBody()).orElse(null),
          responseHeaders,
          statusCode);
    } else {
      return new ResponseEntity<>(
          assetIdOpt.map(o -> assetResponse.getBody()).orElse(null),
          statusCode);

    }
  }


  @PostMapping("asset/download")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Download a set of assets"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_READ)
  public void downloadAsset(
      @RequestBody ScopeAssetsDTO scopeAssets, HttpServletResponse response) throws IOException {
    response.setHeader("Content-Disposition", "attachment; filename=download.zip");
    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    assetsService.download(scopeAssets, response.getOutputStream());
  }

  @GetMapping("/smart-contract")
  @Operation(
      description = "Get the business data catalog smart contract address"
  )
  public String getBusinessDataSmartContractAddress() {
    return businessDataContractAddress;
  }
}
