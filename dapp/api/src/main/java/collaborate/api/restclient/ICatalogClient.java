package collaborate.api.restclient;

import collaborate.api.domain.Data;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "catalog-client", url = "http://localhost:7773/api/v1")
public interface ICatalogClient {

    @Operation(description = "Add data")
    @PostMapping("/data")
    Data add(@RequestBody Data data);
}
