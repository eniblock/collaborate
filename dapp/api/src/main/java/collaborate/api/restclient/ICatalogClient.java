package collaborate.api.restclient;

import collaborate.api.config.FeignCatalogConfiguration;
import collaborate.api.domain.Document;
import collaborate.api.domain.Scope;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "catalog-client", url = "${api.catalog-api-url}", configuration = FeignCatalogConfiguration.class)
public interface ICatalogClient {

    @Operation(description = "Add document")
    @PostMapping("organizations/{organizationId}/datasources/{datasourceId}/documents")
    Document add(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, @RequestBody Document document);

    @Operation(description = "Delete documents by datasource")
    @DeleteMapping("organizations/{organizationId}/datasources/{datasourceId}/documents")
    ResponseEntity<Void> delete(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId);

    @Operation(description = "Get documents by datasource")
    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/documents")
    Page<Document> get(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId);

    @Operation(description = "Get scopes")
    @GetMapping("scopes")
    List<Scope> getScopes();

    @Operation(description = "Get scope")
    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/scopes/{scopeId}")
    Scope getScope(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, @PathVariable("scopeId") UUID scopeId);

    @Operation(description = "Get documents by scope")
    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/scopes/{scopeId}/documents")
    Page<Document> getDocumentsByScope(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("datasourceId") Long datasourceId,
            @PathVariable("scopeId") UUID scopeId,
            Pageable pageable,
            @RequestParam(required = false) String q
    );

    @Operation(description = "Get documents")
    @GetMapping("documents")
    Page<Document> getDocuments(
            Pageable pageable,
            @RequestParam(required = false) String q
    );

    @Operation(description = "Get scope by Id")
    @GetMapping("scopes/{scopeId}")
    Scope getScopeById(@PathVariable("scopeId") UUID scopeId);
}
