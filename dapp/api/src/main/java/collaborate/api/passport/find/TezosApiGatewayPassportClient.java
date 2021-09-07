package collaborate.api.passport.find;

import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.IndexerQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-passport-client")
public interface TezosApiGatewayPassportClient {

  @PostMapping("/tezos_node/storage/{contractAddress}")
  PassportByIdsDTO findPassportsByIds(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<IndexerQuery<String>> request);

}
