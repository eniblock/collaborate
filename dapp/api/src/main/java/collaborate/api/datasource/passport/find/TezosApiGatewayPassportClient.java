package collaborate.api.datasource.passport.find;

import collaborate.api.datasource.nft.model.storage.TokenMetadataResponseDTO;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import collaborate.api.tag.model.storage.TagPair;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-passport-client")
interface TezosApiGatewayPassportClient {

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
  TokenIdByOwnerResponseDTO getTokenIdsByOwner(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<String>> request);

  @PostMapping("/tezos_node/storage/{contractAddress}")
  OwnerByTokenIdResponseDTO getOwnersByTokenIds(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<Integer>> request);

  @PostMapping("/tezos_node/storage/{contractAddress}")
  OperatorsByTokenIdsAndOwnersResponseDTO getOperatorsByTokenIdsAndOwners(
      @PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<MapQuery<TagPair<String, Integer>>> request);

  @PostMapping("/tezos_node/storage/{contractAddress}")
  MultisigNbResponseDTO getMultisigCount(
      @PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<String> request);
}
