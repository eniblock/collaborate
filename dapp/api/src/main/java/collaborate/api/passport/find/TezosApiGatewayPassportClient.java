package collaborate.api.passport.find;

import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-passport-client")
public interface TezosApiGatewayPassportClient {

  @PostMapping("/tezos_node/storage/{contractAddress}")
  PassportsIndexerTagResponseDTO getPassportsIndexer(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<String>> request);

  @PostMapping("/tezos_node/storage/{contractAddress}")
  MultisigTagResponseDTO getMultisigs(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<Integer>> request);

  @PostMapping("/tezos_node/storage/{contractAddress}")
  TokenMetadataResponseDTO getTokenMetadata(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<Integer>> request);

  @PostMapping("/tezos_node/storage/{contractAddress}")
  AllTokensResponseDTO getPassportCount(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<String> request);

  @PostMapping("/tezos_node/storage/{contractAddress}")
  TokenIdByAssetIdsResponseDTO getTokenIdByAssetIds(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<String>> request);
}
