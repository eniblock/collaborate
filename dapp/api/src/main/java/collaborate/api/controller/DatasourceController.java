package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.domain.Datasource;
import collaborate.api.repository.DatasourceRepository;
import collaborate.api.services.DatasourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class DatasourceController {

    private final String ADMIN_AUTHORIZATION = "hasRole('service_provider_administrator')";

    @Autowired
    private DatasourceRepository datasourceRepository;

    @Autowired
    private DatasourceService datasourceService;

    @GetMapping("/api/v1/datasources")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(ADMIN_AUTHORIZATION)
    public HttpEntity<Page<Datasource>> list(Pageable pageable) {

        return ResponseEntity.ok(datasourceRepository.findAll(pageable));
    }

    @PostMapping("/api/v1/datasources")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(ADMIN_AUTHORIZATION)
    public ResponseEntity<Datasource> create(@RequestBody Datasource datasource) {
        dataso@Path("/api/v1/datasources/{id}")urceService.testConnection(datasource);
        datasourceRepository.save(datasource);

        Link link = linkTo(methodOn(DatasourceController.class).get(datasource.getId())).withSelfRel();

        return ResponseEntity.created(link.toUri()).build();
    }

    @GetMapping("/api/v1/datasources/{id}")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(ADMIN_AUTHORIZATION)
    public ResponseEntity<Datasource> get(@PathVariable(value = "id") Long id) {
        Datasource datasource = datasourceRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(datasource);
    }
}
