package collaborate.api.config;

import collaborate.api.config.api.SmartContractAddressProperties;
import collaborate.api.tag.config.TezosApiGatewayConfClient;
import collaborate.api.tag.config.TezosIndexer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@Tag(name = "config", description =
    "The Config API")
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class ConfigController {

  public static final String TZSTATS = "tzstats";
  private final SmartContractAddressProperties smartContractAddressProperties;
  private final TezosApiGatewayConfClient tezosApiGatewayConfClient;

  @GetMapping("/smart-contract")
  @Operation(description = "Get the underlying block chain smart-contracts")
  public SmartContractAddressProperties smartContracts() {
    return smartContractAddressProperties;
  }

  @GetMapping("/tzindex-url")
  @Operation(description = "Get the TzIndex URL. TzIndex is an indexer for tezos block chain")
  public TezosIndexer tzIndexUrl() {
    return tezosApiGatewayConfClient.getConfig()
        .findIndexerUrlByName(TZSTATS)
        .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No tzstats indexer seems to be available"
            )
        );
  }
}
