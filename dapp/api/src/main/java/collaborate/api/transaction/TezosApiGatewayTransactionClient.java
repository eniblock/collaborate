package collaborate.api.transaction;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "tag-transaction-client", url = "${transaction.tag-client.url}/api")
public interface TezosApiGatewayTransactionClient {

  @GetMapping("contract/{contractAddress}/calls")
  List<Transaction> getSmartContractTransactionList(
      @PathVariable String contractAddress,
      @RequestParam(required = false, defaultValue = "0") int offset,
      @RequestParam(required = false, defaultValue = "20") int limit);

}
