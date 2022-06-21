package collaborate.api.ipfs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = "data", description = "The IPFS API")
@RequestMapping("/api/v1/ipfs")
@RequiredArgsConstructor
public class IpfsController {

  private final IpfsService ipfsService;

  @GetMapping("/cat/{cid}")
  @Operation(description = "Show the content of the given cid")
  public Object cat(@PathVariable String cid) {
    return ipfsService.cat(cid, Object.class);
  }

}
