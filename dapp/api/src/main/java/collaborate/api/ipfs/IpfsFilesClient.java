package collaborate.api.ipfs;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

import collaborate.api.config.FeignConfig;
import collaborate.api.ipfs.domain.CidResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @see <a href="https://docs.ipfs.io/reference/http/api/">IPFS HTTP API</a>
 */
@FeignClient(name = "ipfs-files-client", url = "${ipfs.url}/api/v0/files", configuration = FeignConfig.class)
public interface IpfsFilesClient {

  /**
   * Create a folder in the IPFS cache
   *
   * @param createParents When true, make parent directories as needed. No error if parents already
   *                      exist
   * @see <a href="https://docs.ipfs.io/reference/http/api/#api-v0-files-mkdir">docs.ipfs -
   * files-mkdir</a>
   */
  @PostMapping(value = "mkdir")
  Void makeDirectory(@RequestParam("arg") String absolutePath,
      @RequestParam("parents") boolean createParents);

  /**
   * Flush the given path from cache to disk
   *
   * @see <a href="https://docs.ipfs.io/reference/http/api/#api-v0-files-write">IPFS HTTP API</a>
   */
  @PostMapping(value = "flush")
  CidResponse flush(@RequestParam("arg") String absolutePath);

  /**
   * Write to a mutable file in a given filesystem.
   *
   * @param absolutePath Path to write to
   * @param create       Create the file if it does not exist, parent folder won't be created
   * @see <a href="https://docs.ipfs.io/reference/http/api/#api-v0-files-write">docs.ipfs -
   * * files-write</a>
   */
  @PostMapping(value = "write", consumes = MULTIPART_FORM_DATA_VALUE)
  Void write(@RequestParam("arg") String absolutePath,
      @RequestParam("create") boolean create,
      @RequestPart(value = "data") MultipartFile file);
}
