package collaborate.api.controller;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.domain.Document;
import collaborate.api.restclient.ICatalogClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class DocumentController {

    private final String OPERATOR_AUTHORIZATION = "hasRole('service_provider_operator')";

    @Autowired
    private ICatalogClient catalogClient;

    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/scopes/{scopeId}/documents")
    @Operation(
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK)
    )
    @PreAuthorize(OPERATOR_AUTHORIZATION)
    public HttpEntity<Page<Document>> listByScope(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, @PathVariable("scopeId") UUID scopeId, Pageable pageable, @RequestParam(required = false) String q) {
        Page<Document> documents = catalogClient.getDocumentsByScope(organizationId, datasourceId, scopeId, pageable, q);

        return ResponseEntity.ok(documents);
    }

    @PostMapping("documents/{id}/downloads")
    public void download(@PathVariable("id") String id) {

    }

    @PostMapping("downloads")
    public void downloadList(@RequestBody Document[] documents) {

    }
}
