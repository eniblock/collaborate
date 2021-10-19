package collaborate.api.ipfs;

import collaborate.api.config.FeignConfig;
import collaborate.api.ipfs.domain.IpnsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Relate to <a href="https://docs.ipfs.io/concepts/ipns/">IPNS</a>
 *
 * @see <a href="https://docs.ipfs.io/reference/http/api/">IPFS HTTP API</a>
 */
@FeignClient(name = "ipfs-name-client", url = "${ipfs.url}/api/v0/name", configuration = FeignConfig.class)
public interface IpfsNameClient {

  /**
   * Publish IPNS names
   *
   * @see <a href="https://docs.ipfs.io/reference/http/api/#api-v0-name-publish">docs.ipfs -
   * name/publish</a>
   */
  @PostMapping(value = "publish")
  IpnsResponse publish(@RequestParam("arg") String cid);

  /**
   * Update IPNS symbolic link reference
   *
   * @see <a href="https://docs.ipfs.io/reference/http/api/">IPFS HTTP API</a>
   */
  @PostMapping(value = "publish")
  IpnsResponse update(@RequestParam("arg") String absolutePath, @RequestParam("key") String key);
}
