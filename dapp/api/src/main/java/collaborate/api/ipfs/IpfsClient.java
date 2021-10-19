package collaborate.api.ipfs;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import collaborate.api.config.FeignConfig;
import collaborate.api.ipfs.domain.AddResponse;
import collaborate.api.ipfs.domain.IpnsResponse;
import collaborate.api.ipfs.domain.LsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @see <a href="https://docs.ipfs.io/reference/http/api/">IPFS HTTP API</a>
 */
@FeignClient(name = "ipfs-client", url = "${ipfs.url}/api/v0", configuration = FeignConfig.class)
public interface IpfsClient {

  /**
   * Add a file or directory to IPFS.<br> The file is automatically pinned
   */
  @PostMapping(value = "add", consumes = MULTIPART_FORM_DATA_VALUE)
  AddResponse add(@RequestPart(value = "file") MultipartFile file);

  @PostMapping("cat")
  String cat(@RequestParam("arg") String cid);

  @PostMapping("ls")
  LsResponse listDirectoryContent(@RequestParam("arg") String cid);

  @PostMapping("/name/publish")
  IpnsResponse publish(@RequestParam("arg") String cid);

}
