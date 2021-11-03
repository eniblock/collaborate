package collaborate.api.businessdata.find;

import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-business-data-client")
public interface TAGBusinessDataClient {

  @PostMapping("/tezos_node/storage/{contractAddress}")
  IndexerTagResponseDTO getPassportsIndexer(
      @PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<String>> request);
}
