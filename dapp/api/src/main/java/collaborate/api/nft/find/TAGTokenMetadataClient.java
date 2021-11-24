package collaborate.api.nft.find;

import collaborate.api.nft.model.storage.TokenMetadataResponseDTO;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-token-metadata-client")
public interface TAGTokenMetadataClient {

  @PostMapping("/tezos_node/storage/{contractAddress}")
  TokenMetadataResponseDTO getTokenMetadata(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<Integer>> request);

}