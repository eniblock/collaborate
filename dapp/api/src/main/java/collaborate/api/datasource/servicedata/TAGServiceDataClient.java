package collaborate.api.datasource.servicedata;

import collaborate.api.datasource.servicedata.access.model.AccessRequestTagResponseDTO;
import collaborate.api.datasource.businessdata.find.IndexerTagResponseDTO;
import collaborate.api.tag.model.TokenMetadataResponseDTO;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-service-data-client")
public interface TAGServiceDataClient {

  @PostMapping("/tezos_node/storage/{contractAddress}")
  IndexerTagResponseDTO getIndexer(
      @PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<String>> request);

  @PostMapping("/tezos_node/storage/{contractAddress}")
  TokenMetadataResponseDTO getTokenMetadata(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<Integer>> request);

  @PostMapping("/tezos_node/storage/{contractAddress}")
  AccessRequestTagResponseDTO getAccessRequests(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<UUID>> request);

}
