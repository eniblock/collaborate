package collaborate.api.datasource.passport.create;

import collaborate.api.datasource.nft.model.storage.TokenMetadata;
import collaborate.api.datasource.nft.model.storage.TokenMetadataResponseDTO;
import collaborate.api.datasource.passport.find.AllTokensResponseDTO;
import collaborate.api.datasource.passport.find.MultisigTagResponseDTO;
import collaborate.api.datasource.passport.find.PassportsIndexerTagResponseDTO;
import collaborate.api.datasource.passport.find.TokenIdByAssetIdsResponseDTO;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(url = "${tezos-api-gateway.url}/api", name = "tag-passport-creation-client")
interface TezosApiGatewayPassportCreationClient {

  @PostMapping("/tezos_node/storage/{contractAddress}")
  MultisigNbResponseDTO getMultisigNb(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<String> request);

}
