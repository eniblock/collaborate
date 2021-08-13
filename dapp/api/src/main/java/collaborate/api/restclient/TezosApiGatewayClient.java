package collaborate.api.restclient;

import collaborate.api.domain.Job;
import collaborate.api.domain.TransactionBatch;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tag-client", url = "${tezos-api-gateway.url}/api")
public interface TezosApiGatewayClient {

    @Operation(description = "Send transaction batch")
    @PostMapping("send/jobs")
    <T> Job sendTransactionBatch(@RequestBody TransactionBatch<T> transactions);
}
