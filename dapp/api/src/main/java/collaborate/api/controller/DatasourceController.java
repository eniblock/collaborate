package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.enumeration.DatasourceEvent;
import collaborate.api.repository.DatasourceRepository;
import collaborate.api.services.DatasourceService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
@RequestMapping("datasources")
public class DatasourceController {

    private final String ADMIN_AUTHORIZATION = "hasRole('service_provider_administrator')";

    @Autowired
    private DatasourceRepository datasourceRepository;

    @Autowired
    private DatasourceService datasourceService;

    @GetMapping()
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(ADMIN_AUTHORIZATION)
    public HttpEntity<Page<Datasource>> list(Pageable pageable) {

        return ResponseEntity.ok(datasourceRepository.findAll(pageable));
    }

    @PostMapping()
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(ADMIN_AUTHORIZATION)
    public ResponseEntity<Datasource> create(@RequestBody Datasource datasource) throws JsonProcessingException {
        // Test datasource connection
        datasourceService.testConnection(datasource);

        // Save the datasource in DB
        datasource = datasourceRepository.save(datasource);

        // TODO save client id and client secret in vault

        // Send synchronize datasource message
        datasourceService.produce(datasource, DatasourceEvent.CREATED);

        Link link = linkTo(methodOn(DatasourceController.class).get(datasource.getId())).withSelfRel();

        return ResponseEntity.created(link.toUri()).build();
    }

    @GetMapping("{id}")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(ADMIN_AUTHORIZATION)
    public ResponseEntity<Datasource> get(@PathVariable(value = "id") Long id) {
        Datasource datasource = datasourceRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(datasource);
    }
}
