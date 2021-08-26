package collaborate.api.tag;

import collaborate.api.tag.model.Job;
import collaborate.api.tag.model.TransactionBatch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tag-job-client", url = "${tezos-api-gateway.url}/api")
public interface TezosApiGatewayJobClient {

  @PostMapping("send/jobs")
  <T> Job sendTransactionBatch(@RequestBody TransactionBatch<T> transactions);

}
