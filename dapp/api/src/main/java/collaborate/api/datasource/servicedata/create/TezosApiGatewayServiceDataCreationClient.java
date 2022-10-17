package collaborate.api.datasource.servicedata.create;

import collaborate.api.tag.model.storage.DataFieldsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-servicedata-creation-client")
interface TezosApiGatewayServiceDataCreationClient {

  @PostMapping("/tezos_node/storage/{contractAddress}")
  MultisigNbResponseDTO getMultisigNb(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<String> request);

}
