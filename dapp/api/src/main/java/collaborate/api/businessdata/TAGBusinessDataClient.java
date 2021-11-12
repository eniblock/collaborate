package collaborate.api.businessdata;

import collaborate.api.businessdata.access.model.AccessRequestTagResponseDTO;
import collaborate.api.businessdata.find.IndexerTagResponseDTO;
import collaborate.api.nft.model.storage.TokenMetadataResponseDTO;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-business-data-client")
public interface TAGBusinessDataClient {

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
