package collaborate.api.datasource.servicedata;

import static org.springframework.data.domain.Sort.Direction.DESC;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.datasource.servicedata.model.ServiceData;
import collaborate.api.datasource.servicedata.model.ServiceDataDTO;
import collaborate.api.datasource.servicedata.access.GrantSubscribeService;
import collaborate.api.datasource.servicedata.access.SubscribeRequestService;
import collaborate.api.datasource.servicedata.access.model.AccessRequestDTO;
//import collaborate.api.datasource.servicedata.document.ServiceDataAssetsService;
//import collaborate.api.datasource.servicedata.document.model.ServiceDataNFTSummary;
import collaborate.api.datasource.servicedata.find.ServiceDataAssetDetailsService;
import collaborate.api.datasource.servicedata.model.ServiceDataAssetDetailsDTO;
import collaborate.api.datasource.businessdata.access.model.ClientIdAndSecret;
//import collaborate.api.datasource.nft.catalog.NftServiceDataService;
import collaborate.api.datasource.nft.model.AssetDetailsDTO;
//import collaborate.api.datasource.nft.model.storage.TokenIndex;
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
@Tag(name = "service-data", description = "the service-data API")
@RequestMapping("/api/v1/service-data")
@Validated
public class ServiceDataController {

//  private final ServiceDataAssetsService assetsService;
  private final ServiceDataAssetDetailsService assetDetailsService;
  private final String serviceDataContractAddress;
  private final ServiceDataService serviceDataService;  
//  private final NftServiceDataService nftServiceDataService;
//  private final ServiceDataNftService nftService;
  private final SubscribeRequestService subscribeRequestService;
  private final GrantSubscribeService grantSubscribeService;
  
  @PostMapping
  @Operation(
      description = "Generate a datasource configuration and publish it on IPFS."
          + "When the datasource is for business data, the associated scope are also minted as NFT business data token",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DSP)
  public ResponseEntity<ServiceData> createServiceData(@RequestBody @Valid ServiceDataDTO serviceDataDTO) throws IOException {
    var result = serviceDataService.create(serviceDataDTO);
    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  @GetMapping("market-place")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the service data catalog where assets are not owned by the current organization"
  )
  @PreAuthorize(HasRoles.SERVICE_DATA_READ)
  public Page<ServiceDataAssetDetailsDTO> marketPlace(
      @PageableDefault(sort = {"nftId"}, direction = DESC) @ParameterObject Pageable pageable,
      @RequestParam Map<String, String> allParams
  ) {
    // Pageable attribute needs to be excluded from the market-place filters
    var excludedKeys = List.of("page", "size");
    var filters = allParams.entrySet().stream()
        .filter(entry -> !excludedKeys.contains(entry.getKey().toLowerCase()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    return assetDetailsService.marketPlace(filters, pageable);
  }

  @GetMapping("{serviceId}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the service data catalog (list of scopes)"
  )
  @PreAuthorize(HasRoles.SERVICE_DATA_READ)
  public ServiceData listAssetDetails(
      @PathVariable String serviceId
  ) {
    return assetDetailsService.find(serviceId);
  }
  

  @PostMapping("access-request")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Make a grant access request for the given tokens"
  )
  @PreAuthorize(HasRoles.SERVICE_DATA_GRANT_ACCESS_REQUEST)
  public Job requestAccess(
      @RequestBody @NotEmpty List<@Valid AccessRequestDTO> accessRequestDTOs) {
    return subscribeRequestService.requestAccess(accessRequestDTOs);
  }


/*

  @PostMapping("/access/{serviceId}/access/{organization}")
  @Operation(
      description = "Add credentials for making the given organization able to access to the serviceId",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DSP_ADMIN)
  public void addAccess(
      @PathVariable(value = "organization") String requesterAddress,
      @PathVariable(value = "serviceId") Integer serviceId,
      @RequestBody ClientIdAndSecret clientIdAndSecret
  ) {
    //nftService.findOneByNftId(nftId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NftId not managed by this organization"));

    grantSubscribeService.grant(serviceDataContractAddress, requesterAddress, serviceId, clientIdAndSecret);
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

  @GetMapping("asset/{tokenId}/summary")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get some basics informations for the given NFT"
  )
  @PreAuthorize(HasRoles.SERVICE_DATA_READ)
  public ServiceDataNFTSummary getSummary(@PathVariable Integer tokenId)
      throws InterruptedException {
    nftServiceDataService.saveConfigurationByTokenId(tokenId, serviceDataContractAddress);
    return assetsService.getSummary(tokenId);
  }

  @GetMapping("asset/{tokenId}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "See all the service-data assets (documents) of the specified token id"
  )
  @PreAuthorize(HasRoles.SERVICE_DATA_READ)
  public Page<ServiceData> listAssetDocuments(@PathVariable Integer tokenId,
      @ParameterObject Pageable pageable)
      throws InterruptedException {
    nftServiceDataService.saveConfigurationByTokenId(tokenId, serviceDataContractAddress);
    return assetsService.listScopeAssets(tokenId, pageable);
  }
*/
}
