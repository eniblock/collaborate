package collaborate.api.ipfs;

import collaborate.api.config.FeignConfig;
import collaborate.api.ipfs.domain.pin.PinAddResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @see <a href="https://docs.ipfs.io/reference/http/api/">IPFS HTTP API</a>
 */
@FeignClient(name = "ipfs-pin-client", url = "${ipfs.url}/api/v0/pin", configuration = FeignConfig.class)
public interface IpfsPinClient {

  /**
   * Pin objects to local storage
   *
   * @param recursive Recursively pin the object linked to by the specified object(s) When true,
   *                  make parent directories as needed. No error if parents already exist
   * @see <a href="https://docs.ipfs.io/reference/http/api/#api-v0-pin-add">docs.ipfs -
   * pin/add</a>
   */
  @PostMapping(value = "add")
  PinAddResponse add(@RequestParam("arg") String cid, boolean recursive);

  /**
   * @see #add(String, boolean) where recursive is true
   */
  @PostMapping(value = "add")
  PinAddResponse add(@RequestParam("arg") String cid);

}
