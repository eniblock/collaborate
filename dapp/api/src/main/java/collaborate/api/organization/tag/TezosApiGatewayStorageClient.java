package collaborate.api.organization.tag;

import collaborate.api.tag.model.storage.DataFieldsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tag-storage-client", url = "${tezos-api-gateway.url}/api")
public interface TezosApiGatewayStorageClient {

  @PostMapping("tezos_node/storage/{contractAddress}")
  OrganizationMap getOrganizations(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<String> selectQuery);

}
