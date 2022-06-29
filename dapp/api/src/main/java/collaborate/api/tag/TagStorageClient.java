package collaborate.api.tag;

import collaborate.api.tag.model.TokenMetadataResponseDTO;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tag-tezos-storage-client", url = "${tezos-api-gateway.url}/api")
public interface TagStorageClient {

  @PostMapping("/tezos_node/storage/{contractAddress}")
  JsonNode getFields(
      @PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<String> request
  );

  @PostMapping("/tezos_node/storage/{contractAddress}")
  TokenMetadataResponseDTO getTokenMetadata(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<Integer>> request);

}
