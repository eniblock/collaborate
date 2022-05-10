package collaborate.api.datasource;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.datasource.create.CreateDatasourceService;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceDetailsDto;
import collaborate.api.datasource.model.dto.DatasourceVisitorException;
import collaborate.api.datasource.model.dto.ListDatasourceDTO;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@Tag(name = "datasources", description =
    "The Datasource API. Data source is the core concept used by the data-gateway, "
        + "used to connect the application to external services (ex: REST API)")
@RequestMapping("/api/v1/datasources")
@RequiredArgsConstructor
public class DatasourceController {

  private final DatasourceService datasourceService;
  private final CreateDatasourceService createDatasourceService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      description = "Generate a datasource configuration and publish it on IPFS."
          + "When the datasource is for business data, the associated scope are also minted as NFT business data token",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DSP_ADMIN)
  public ResponseEntity<Datasource> createDatasource(
      @RequestPart("datasource") DatasourceDTO datasource,
      @RequestPart("pfxFile") Optional<MultipartFile> pfxFile)
      throws IOException, DatasourceVisitorException {
    var violations = Validation.buildDefaultValidatorFactory()
        .getValidator()
        .validate(datasource);
    if (!CollectionUtils.isEmpty(violations)) {
      throw new ResponseStatusException(BAD_REQUEST, "",
          new ConstraintViolationException(violations));
    }
    testDatasourceConnection(datasource, pfxFile);
    var datasourceResult = createDatasourceService.create(datasource, pfxFile);
    return new ResponseEntity<>(datasourceResult, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Operation(
      description = "Get datasource details",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DATASOURCE_READ)
  public ResponseEntity<DatasourceDetailsDto> getDatasourceById(
      @PathVariable(value = "id") UUID id) {
    return datasourceService.findDetailsById(id.toString())
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  @Operation(
      description = "Get the list of created datasources",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DATASOURCE_READ)
  public HttpEntity<Page<ListDatasourceDTO>> listDatasources(
      @SortDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
      @RequestParam(required = false, defaultValue = "") String query) {
    Page<ListDatasourceDTO> datasourcePage = datasourceService.findAll(pageable, query);
    return ResponseEntity.ok(datasourcePage);
  }

  @GetMapping("/{id}/scopes")
  @Operation(
      description = "Get scopes associated to a datasource",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DATASOURCE_READ)
  public ResponseEntity<Set<String>> listScopesByDatasourceId(
      @PathVariable(value = "id") String id) {
    var scopesOpt = datasourceService.getResourcesByDataSourceId(id);
    return scopesOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping(value = "test-connection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      description = "Try to communicate to the underlying datasource asset-list entry-point",
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DSP_ADMIN)
  public ResponseEntity<Void> testDatasourceConnection(
      @RequestPart("datasource") DatasourceDTO datasource,
      @RequestPart("pfxFile") Optional<MultipartFile> pfxFile)
      throws IOException, DatasourceVisitorException {
    if (createDatasourceService.testConnection(datasource, pfxFile)) {
      return ResponseEntity.ok().build();
    } else {
      log.info("Test connection failed");
      throw new ResponseStatusException(BAD_REQUEST, "Test connection failed");
    }
  }

}
