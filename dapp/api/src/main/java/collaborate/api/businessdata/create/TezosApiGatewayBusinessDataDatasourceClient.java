package collaborate.api.businessdata.create;

import collaborate.api.datasource.create.MultisigCounterResponseDTO;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-business-datasource-client")
public interface TezosApiGatewayBusinessDataDatasourceClient {

  @PostMapping("/tezos_node/storage/{contractAddress}")
  MultisigCounterResponseDTO getMultisigCounter(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<String> request);
}
