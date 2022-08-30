package collaborate.api.datasource.businessdata;

import static org.springframework.data.domain.Sort.Direction.DESC;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.datasource.businessdata.access.AccessRequestService;
import collaborate.api.datasource.businessdata.access.GrantAccessService;
import collaborate.api.datasource.businessdata.access.model.AccessRequestDTO;
import collaborate.api.datasource.businessdata.access.model.ClientIdAndSecret;
import collaborate.api.datasource.businessdata.document.AssetsService;
import collaborate.api.datasource.businessdata.document.model.BusinessDataDocument;
import collaborate.api.datasource.businessdata.document.model.BusinessDataNFTSummary;
import collaborate.api.datasource.businessdata.document.model.ScopeAssetsDTO;
import collaborate.api.datasource.businessdata.find.AssetDetailsService;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.tag.model.job.Job;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

  private final AccessRequestService accessRequestService;
  private final AssetsService assetsService;
  private final AssetDetailsService assetDetailsService;
  private final String businessDataContractAddress;
  private final GrantAccessService grantAccessService;
  private final NftDatasourceService nftDatasourceService;
  private final NftService nftService;

  @GetMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the business data catalog (list of scopes)"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_READ)
  public Page<AssetDetailsDTO> listAssetDetails(
      @PageableDefault(sort = {"tokenId"}, direction = DESC) @ParameterObject Pageable pageable,
      @RequestParam(required = false) Optional<String> assetId,
      @RequestParam(required = false) Optional<String> assetOwner
  ) {
    var predicate = assetId.map(q -> (Predicate<TokenIndex>) t -> t.getAssetId().contains(q));
    return assetDetailsService.find(pageable, predicate, assetOwner);
  }

  @GetMapping("market-place")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the business data catalog where assets are not owned by the current organization"
  )
  @PreAuthorize(HasRoles.BUSINESS_DATA_READ)
  public Page<AssetDetailsDTO> marketPlace(
      @PageableDefault(sort = {"tokenId"}, direction = DESC) @ParameterObject Pageable pageable,
      @RequestParam Map<String, String> allParams
  ) {
    // Pageable attribute needs to be excluded from the market-place filters
    var excludedKeys = List.of("page", "size");
    var filters = allParams.entrySet().stream()
        .filter(entry -> !excludedKeys.contains(entry.getKey().toLowerCase()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    return assetDetailsService.marketPlace(filters, pageable);
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
    if (nftDatasourceService.saveConfigurationByTokenId(
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
  public BusinessDataNFTSummary getSummary(@PathVariable Integer tokenId)
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
  public Page<BusinessDataDocument> listAssetDocuments(@PathVariable Integer tokenId,
      @ParameterObject Pageable pageable)
      throws InterruptedException {
    waitForDatasourceConfiguration(tokenId);
    return assetsService.listScopeAssets(tokenId, pageable);
  }

  @GetMapping(value = {
      "asset/{tokenId}/test-connection",
      "asset/{tokenId}/fetch/{documentId}"}
  )
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
      @PathVariable Optional<String> documentId)
      throws InterruptedException {
    waitForDatasourceConfiguration(tokenId);

    var assetResponse = assetsService.fetch(tokenId, documentId);
    var statusCode = assetResponse.getStatusCode();
    if (statusCode == HttpStatus.SERVICE_UNAVAILABLE) {
      statusCode = HttpStatus.BAD_GATEWAY;
    }

    var contentType = assetResponse.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
    if (contentType != null) {
      HttpHeaders responseHeaders = new HttpHeaders();
      responseHeaders.put(HttpHeaders.CONTENT_TYPE, List.of(contentType));
      return new ResponseEntity<>(
          documentId.map(o -> assetResponse.getBody()).orElse(null),
          responseHeaders,
          statusCode);
    } else {
      return new ResponseEntity<>(
          documentId.map(o -> assetResponse.getBody()).orElse(null),
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

  @PostMapping("/asset/{tokenId}/access/{organization}")
  @Operation(
      description = "Add credentials for making the given organization able to access to the tokenId",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DSP_ADMIN)
  public void addAccess(
      @PathVariable(value = "organization") String requesterAddress,
      @PathVariable(value = "tokenId") Integer nftId,
      @RequestBody ClientIdAndSecret clientIdAndSecret
  ) {
    nftService.findOneByNftId(nftId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "NftId not managed by this organization"));

    grantAccessService.grant(businessDataContractAddress, requesterAddress, nftId,
        clientIdAndSecret);
  }
}
