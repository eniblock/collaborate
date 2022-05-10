package collaborate.api.ipfs;

import collaborate.api.config.api.SmartContractAddressProperties;
import collaborate.api.datasource.nft.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@Tag(name = "config", description = "The IPFS API")
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
