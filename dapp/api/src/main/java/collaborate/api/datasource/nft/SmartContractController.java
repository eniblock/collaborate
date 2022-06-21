package collaborate.api.datasource.nft;

import collaborate.api.config.api.SmartContractAddressProperties;
import com.fasterxml.jackson.databind.JsonNode;
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
@Tag(name = "smart-contract", description = "The smart-contract API")
@RequestMapping("/api/v1/smart-contract")
@RequiredArgsConstructor
public class SmartContractController {

  private final SmartContractAddressProperties smartContractAddress;
  private final TokenService tokenService;

  @GetMapping("/{contractAddress}/token/{tokenId}/on-chain-metadata")
  @Operation(description = "Get the given token on-chain metadata")
  public Map<String, String> getOnChainMetadataByTokenId(
      @PathVariable String contractAddress,
      @PathVariable Integer tokenId) {
    return tokenService.getOnChainMetadataByTokenId(contractAddress, tokenId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @GetMapping("/{contractAddress}/token/{tokenId}/off-chain-metadata")
  @Operation(description = "Get the given token off-chain metadata (the TZip21 content)")
  public Map<String, JsonNode> getOffChainMetadataByTokenId(
      @PathVariable String contractAddress,
      @PathVariable Integer tokenId) {
    return tokenService.getOffChainMetadataByTokenId(contractAddress, tokenId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

}
