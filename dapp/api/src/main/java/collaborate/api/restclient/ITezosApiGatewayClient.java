package collaborate.api.restclient;

import collaborate.api.config.FeignConfiguration;
import collaborate.api.domain.Job;
import collaborate.api.domain.TransactionBatch;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tezos-api-gateway-client", url = "http://localhost:3333/api", configuration = FeignConfiguration.class)
public interface ITezosApiGatewayClient {

    @Operation(description = "Send transaction batch")
    @PostMapping("send/jobs")
    <T> Job sendTransactionBatch(@RequestBody TransactionBatch<T> transactions);
}
