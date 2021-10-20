package collaborate.api.ipfs;

import collaborate.api.config.FeignConfig;
import collaborate.api.ipfs.domain.KeyPair;
import collaborate.api.ipfs.domain.ListKeyPairResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @see <a href="https://docs.ipfs.io/reference/http/api/">IPFS HTTP API</a>
 */
@FeignClient(name = "ipfs-key-client", url = "${ipfs.url}/api/v0/key", configuration = FeignConfig.class)
public interface IpfsKeyClient {

  String SELF = "self";

  /**
   * Create new key pair
   *
   * @see <a href="https://docs.ipfs.io/reference/http/api/#api-v0-key-gen">docs.ipfs -
   * key/gen</a>
   */
  @PostMapping(value = "gen")
  KeyPair createKeyPair(@RequestParam("arg") String name);

  /**
   * List all local keypairs
   *
   * @see <a href="https://docs.ipfs.io/reference/http/api/#api-v0-key-gen">docs.ipfs -
   * key/gen</a>
   */
  @PostMapping(value = "list")
  ListKeyPairResponse getAllKeyPairs();

}
