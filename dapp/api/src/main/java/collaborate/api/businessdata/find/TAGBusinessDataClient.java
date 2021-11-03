package collaborate.api.businessdata.find;

import collaborate.api.nft.model.storage.TokenMetadata;
import collaborate.api.passport.find.AllTokensResponseDTO;
import collaborate.api.passport.find.MultisigTagResponseDTO;
import collaborate.api.passport.find.PassportsIndexerTagResponseDTO;
import collaborate.api.passport.find.TokenIdByAssetIdsResponseDTO;
import collaborate.api.passport.find.TokenMetadataResponseDTO;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-passport-client")
public interface TAGBusinessDataClient {

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

  @PostMapping("/tezos_node/storage/{contractAddress}")
  TokenMetadata getTokenMetadataByTokenId(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<String>> request);
}
