package collaborate.api.config;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.config.api.SmartContractAddressProperties;
import collaborate.api.config.model.Onboarding;
import collaborate.api.tag.TagService;
import collaborate.api.tag.config.TagConfig;
import collaborate.api.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = "config", description =
    "The Config API")
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
public class ConfigController {

  private final SmartContractAddressProperties smartContractAddressProperties;
  private final ApiProperties apiProperties;
  private final TagService tagService;
  private final UserService userService;

  @GetMapping("/smart-contract")
  @Operation(description = "Get the underlying block chain smart-contracts")
  public SmartContractAddressProperties smartContracts() {
    return smartContractAddressProperties;
  }

  @GetMapping("/tezos-api-gateway")
  @Operation(description = "Get the Tezos API Gateway configuration")
  public TagConfig tzIndexUrl() {
    return tagService.getConfig();
  }

  @GetMapping("/onboarding")
  @Operation(description = "Get the organization onboarding informartion")
  public Onboarding onboarding() {
    return Onboarding.builder()
        .publicEncryptionKey(apiProperties.getPublicEncryptionKey())
        .walletAddress(userService.getAdminUser().getAddress())
        .build();
  }
}
