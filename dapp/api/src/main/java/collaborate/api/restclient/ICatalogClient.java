package collaborate.api.restclient;

import collaborate.api.config.FeignConfiguration;
import collaborate.api.domain.Document;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "catalog-client", url = "http://localhost:7773/api/v1", configuration = FeignConfiguration.class)
public interface ICatalogClient {

    @Operation(description = "Add document")
    @PostMapping("organizations/{organizationId}/datasources/{datasourceId}/documents")
    Document add(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId, @RequestBody Document document);

    @Operation(description = "Delete document")
    @DeleteMapping("organizations/{organizationId}/datasources/{datasourceId}/documents")
    ResponseEntity<Void> delete(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId);

    @Operation(description = "Get document")
    @GetMapping("organizations/{organizationId}/datasources/{datasourceId}/documents")
    Page<Document> get(@PathVariable("organizationId") String organizationId, @PathVariable("datasourceId") Long datasourceId);
}
