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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public Page<Datasource> list(Pageable pageable) {

        return datasourceRepository.findAll(pageable);
    }

    @PostMapping("/api/v1/datasources")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    public Datasource create(@RequestBody Datasource datasource) {
        datasourceService.testConnection(datasource);

        datasourceRepository.save(datasource);

        return datasource;
    }
}
