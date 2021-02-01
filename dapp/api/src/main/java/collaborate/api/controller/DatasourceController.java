package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.domain.Datasource;
import collaborate.api.domain.DatasourceClientSecret;
import collaborate.api.domain.enumeration.DatasourceEvent;
import collaborate.api.repository.DatasourceRepository;
import collaborate.api.services.DatasourceService;
import collaborate.api.services.dto.DatasourceDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.vault.core.VaultKeyValueOperations;
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

    @Autowired
    private VaultKeyValueOperations vaultKeyValueOperations;

    private static final ModelMapper modelMapper = new ModelMapper();

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
    public ResponseEntity<Datasource> create(@RequestBody DatasourceDTO datasourceDTO) throws JsonProcessingException {
        Datasource datasource = modelMapper.map(datasourceDTO, Datasource.class);
        DatasourceClientSecret datasourceClientSecret = modelMapper.map(datasourceDTO, DatasourceClientSecret.class);

        // Test datasource connection
        datasourceService.testConnection(datasource, datasourceClientSecret);

        // Save the datasource in DB
        datasource = datasourceRepository.save(datasource);

        // Save client secret in vault
        vaultKeyValueOperations.put("datasources/" + datasource.getId(), datasourceClientSecret);

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
