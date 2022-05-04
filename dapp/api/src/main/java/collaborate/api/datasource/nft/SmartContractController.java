package collaborate.api.datasource.nft;

import collaborate.api.config.api.SmartContractAddressProperties;
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
@Tag(name = "config", description ="The smart-contract API")
@RequestMapping("/api/v1/smart-contract")
@RequiredArgsConstructor
public class SmartContractController {

  private final SmartContractAddressProperties smartContractAddress;
  private final TokenService tokenService;

  @GetMapping("/address")
  @Operation(description = "Get the smart-contract addresses")
  public SmartContractAddressProperties getAddress() {
    return smartContractAddress;
  }

  @GetMapping("/{contractAddress}/token/{tokenId}/metadata")
  @Operation(description = "Get the metadata for the given token id")
  public Map<String, String> getMetadataByTokenId(
      @PathVariable String contractAddress,
      @PathVariable Integer tokenId) {
    return tokenService.getMetadataByTokenId(contractAddress, tokenId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

}
