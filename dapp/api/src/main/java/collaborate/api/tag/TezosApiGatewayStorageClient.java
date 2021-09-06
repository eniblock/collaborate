package collaborate.api.tag;

import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.IndexerQuery;
import collaborate.api.tag.model.storage.IndexerQueryResponse;
import collaborate.api.tag.model.storage.IndexerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tag-storage-client", url = "${tezos-api-gateway.url}/api")
public interface TezosApiGatewayStorageClient {

  @PostMapping("tezos_node/storage/{contractAddress}")
  IndexerResponse getIndexer(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<String> selectQuery);

  @PostMapping("tezos_node/storage/{contractAddress}")
  <T,I> IndexerQueryResponse<T> queryIndexer(@PathVariable String contractAddress,
      @RequestBody DataFieldsRequest<IndexerQuery<I>> selectQuery);
}
