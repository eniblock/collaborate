package collaborate.api.ipfs;

import collaborate.api.ipfs.domain.AddResponse;
import collaborate.api.ipfs.domain.IpnsResponse;
import collaborate.api.ipfs.domain.LsResponse;
import collaborate.api.user.security.Authorizations.HasRoles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/v1/ipfs")
@RequiredArgsConstructor
public class IpfsController {

  private final IpfsClient ipfsClient;

  @GetMapping("/ls/{cid}")
  @PreAuthorize(HasRoles.DSP_ADMIN)
  public HttpEntity<LsResponse> list(@PathVariable(value = "cid") String cid) {
    return ResponseEntity.ok(ipfsClient.listDirectoryContent(cid));
  }

  @PostMapping("/add")
  @PreAuthorize(HasRoles.DSP_ADMIN)
  public HttpEntity<AddResponse> get(@RequestPart("file") MultipartFile file) {
    return ResponseEntity.ok(ipfsClient.add(file));
  }

  @GetMapping("/cat/{cid}")
  @PreAuthorize(HasRoles.DSP_ADMIN)
  public HttpEntity<String> cat(@PathVariable(value = "cid") String cid) {
    return ResponseEntity.ok(ipfsClient.cat(cid));
  }

  @PostMapping("/publish/{cid}")
  @PreAuthorize(HasRoles.DSP_ADMIN)
  public ResponseEntity<IpnsResponse> publish(@PathVariable(value = "cid") String cid) {
    return ResponseEntity.ok(ipfsClient.publish(cid));
  }
}
