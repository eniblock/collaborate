package collaborate.api.tag;

import java.util.LinkedHashMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "tag-conf-client", url = "${tezos-api-gateway.url}/api")
public interface TezosApiGatewayConfClient {

  @GetMapping("conf")
  LinkedHashMap<String, Object> getConfiguration();

}
