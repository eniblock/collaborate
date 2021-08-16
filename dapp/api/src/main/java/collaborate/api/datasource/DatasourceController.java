package collaborate.api.datasource;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.datasource.domain.DataSource;
import collaborate.api.datasource.domain.authentication.CertificateBasedBasicAuth;
import collaborate.api.http.security.SSLContextException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("/api/v2/datasources")
@RequiredArgsConstructor
public class DatasourceController {

  private static final String ADMIN_AUTHORIZATION = "hasRole('service_provider_administrator')";
  private final DatasourceService datasourceService;

  @GetMapping
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(ADMIN_AUTHORIZATION)
  public HttpEntity<Page<DataSource>> list(
      @SortDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
      @RequestParam(required = false, defaultValue = "") String query) {
    log.info("Access to datasource list");
    Page<DataSource> datasourcePage = datasourceService.search(pageable, query);
    return ResponseEntity.ok(datasourcePage);
  }

  @GetMapping("/{id}")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(ADMIN_AUTHORIZATION)
  public ResponseEntity<DataSource> get(@PathVariable(value = "id") Long id) {
    log.info("Access to datasource " + id);
    var datasource = datasourceService.findById(id);
    return ResponseEntity.ok(datasource);
  }

  @PostMapping("/basic-auth")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(ADMIN_AUTHORIZATION)
  public ResponseEntity<DataSource> create(
      @RequestPart(value = "pfxFile") MultipartFile pfxFile,
      @RequestPart("datasource") DataSource datasource
  ) throws IOException, SSLContextException {
    log.info("Start creation of a new datasource...");

    // Test datasource connection
    if(datasource.getAuthMethod() instanceof CertificateBasedBasicAuth){
      testBasicAuthConnection(pfxFile, datasource);
    }

    // Save the datasource in DB
    DataSource datasourceResult = datasourceService.create(datasource);

    // Send synchronize datasource message
    // TODO implement with TAG

    log.info("Datasource Created !");

    return new ResponseEntity<>(datasourceResult, HttpStatus.CREATED);
  }

  @PostMapping("/oauth")
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(ADMIN_AUTHORIZATION)
  public ResponseEntity<DataSource> createWithOAuth(
      @RequestBody DataSource datasource
  ){
    log.info("Start creation of a new datasource...");

    // TODO test connection with OAuth

    // Save the datasource in DB
    DataSource datasourceResult = datasourceService.create(datasource);

    // Send synchronize datasource message
    // TODO implement with TAG

    log.info("Datasource Created !");

    return new ResponseEntity<>(datasourceResult, HttpStatus.CREATED);
  }

  @PostMapping(
      value = "test-connection",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
  )
  @PreAuthorize(ADMIN_AUTHORIZATION)
  public ResponseEntity<Void> testBasicAuthConnection(
      @RequestPart("pfxFile") MultipartFile pfxFile,
      @RequestPart("datasource") DataSource datasource
  ) throws IOException, SSLContextException {
    log.info("BasicAuth testing Connection...");

    byte[] pfxFileContent = null;
    if (pfxFile != null) {
      pfxFileContent = pfxFile.getBytes();
    }
    try {
      if (datasourceService.testBasicAuthConnection(datasource, pfxFileContent)) {
        log.info("BasicAuth Connection succeed !");
        return ResponseEntity.ok().build();
      } else {
        log.info("BasicAuth Connection failed !");
        throw new ResponseStatusException(BAD_REQUEST);
      }
    } catch (UnrecoverableKeyException exception) {
      throw new ResponseStatusException(BAD_REQUEST,
          "Provided passphrase can't be used to decrypt private key");
    }
  }

}
