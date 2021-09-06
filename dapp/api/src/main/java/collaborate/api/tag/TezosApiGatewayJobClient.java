package collaborate.api.tag;

import collaborate.api.tag.model.job.Job;
import collaborate.api.tag.model.job.TransactionBatch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tag-job-client", url = "${tezos-api-gateway.url}/api")
public interface TezosApiGatewayJobClient {

  String ORGANIZATION_SECURE_KEY_NAME = "admin";

  @PostMapping("send/jobs")
  <T> Job sendTransactionBatch(@RequestBody TransactionBatch<T> transactions);

}
