package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.domain.Datasource;
import collaborate.api.repository.DatasourceRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
public class DatasourceController {

    private final String ADMIN_AUTHORIZATION = "hasRole('service_provider_administrator')";

    @Autowired
    private DatasourceRepository datasourceRepository;

    private static final ModelMapper modelMapper = new ModelMapper();

    @GetMapping("datasources")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(ADMIN_AUTHORIZATION)
    public HttpEntity<Page<Datasource>> list(@SortDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable, @RequestParam(required = false, defaultValue = "") String q) {
        Page<Datasource> datasourcePage = datasourceRepository.findByNameIgnoreCaseLike(pageable, q);
        return ResponseEntity.ok(datasourcePage);
    }

    @GetMapping("datasources/{id}")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(ADMIN_AUTHORIZATION)
    public ResponseEntity<Datasource> get(@PathVariable(value = "id") Long id) {
        Datasource datasource = datasourceRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(datasource);
    }
}
