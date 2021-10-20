package collaborate.api.datasource;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.datasource.model.Datasource;
import collaborate.api.datasource.model.dto.DatasourceDTO;
import collaborate.api.datasource.model.dto.DatasourceDetailsDto;
import collaborate.api.datasource.model.dto.ListDatasourceDTO;
import collaborate.api.http.security.SSLContextException;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.validation.Valid;
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
@RequestMapping("/api/v1/datasources")
@RequiredArgsConstructor
public class DatasourceController {

  private final DatasourceService datasourceService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DSP_ADMIN)
  public ResponseEntity<Datasource> create(
      @Valid @RequestPart("datasource") DatasourceDTO datasource,
      @RequestPart("pfxFile") Optional<MultipartFile> pfxFile)
      throws Exception {
    testConnection(datasource, pfxFile);
    var datasourceResult = datasourceService.create(datasource, pfxFile);
    return new ResponseEntity<>(datasourceResult, HttpStatus.CREATED);
  }


  @GetMapping("/{id}")
  @Operation(security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DATASOURCE_READ)
  public ResponseEntity<DatasourceDetailsDto> getById(@PathVariable(value = "id") UUID id) {
    return datasourceService.findDetailsById(id.toString())
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  @Operation(security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DATASOURCE_READ)
  public HttpEntity<Page<ListDatasourceDTO>> list(
      @SortDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
      @RequestParam(required = false, defaultValue = "") String query) {
    Page<ListDatasourceDTO> datasourcePage = datasourceService.findAll(pageable, query);
    return ResponseEntity.ok(datasourcePage);
  }

  @GetMapping("/{id}/scopes")
  @Operation(security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DATASOURCE_READ)
  public ResponseEntity<Set<String>> listScopesByDatasourceId(
      @PathVariable(value = "id") String id) {
    var scopesOpt = datasourceService.getScopesByDataSourceId(id);
    return scopesOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping(value = "test-connection", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK))
  @PreAuthorize(HasRoles.DSP_ADMIN)
  public ResponseEntity<Void> testConnection(
      @RequestPart("datasource") DatasourceDTO datasource,
      @RequestPart("pfxFile") Optional<MultipartFile> pfxFile)
      throws IOException, SSLContextException {
    try {
      if (datasourceService.testConnection(datasource, pfxFile)) {
        return ResponseEntity.ok().build();
      } else {
        log.info("Test connection failed");
        throw new ResponseStatusException(BAD_REQUEST, "Test connection failed");
      }
    } catch (UnrecoverableKeyException exception) {
      log.info("Passphrase", exception);
      throw new ResponseStatusException(
          BAD_REQUEST, "Provided passphrase can't be used to decrypt private key");
    }
  }

}
