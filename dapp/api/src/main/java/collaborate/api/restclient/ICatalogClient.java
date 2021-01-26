package collaborate.api.restclient;

import collaborate.api.config.FeignConfiguration;
import collaborate.api.domain.Data;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "catalog-client", url = "http://localhost:7773/api/v1", configuration = FeignConfiguration.class)
public interface ICatalogClient {

    @Operation(description = "Add data")
    @PostMapping("organizations/{organizationName}/datasources/{datasourceId}/data")
    Data add(@PathVariable("organizationName") String organizationName, @PathVariable("datasourceId") Long datasourceId, @RequestBody Data data);

    @Operation(description = "Delete data")
    @DeleteMapping("organizations/{organizationName}/datasources/{datasourceId}/data")
    Data delete(@PathVariable("organizationName") String organizationName, @PathVariable("datasourceId") Long datasourceId);

    @Operation(description = "Get data")
    @GetMapping("organizations/{organizationName}/datasources/{datasourceId}/data")
    Page<Data> get(@PathVariable("organizationName") String organizationName, @PathVariable("datasourceId") Long datasourceId);
}
