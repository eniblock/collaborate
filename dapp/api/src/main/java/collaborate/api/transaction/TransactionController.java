package collaborate.api.transaction;

import collaborate.api.config.OpenApiConfig;
import collaborate.api.user.security.Authorizations.HasRoles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "activities", description = "the Activities API")
@RequestMapping("/api/v1/activities")
public class TransactionController {

  private final TransactionService transactionService;

  @GetMapping()
  @Operation(
      security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEMES_KEYCLOAK),
      description = "Get the block-chain transaction made by the current organization on the platform smart-contracts",
      tags = {"organization"}
  )
  @PreAuthorize(HasRoles.ORGANIZATION_READ)
  public Page<Transaction> currentOrganizationActivities(
      @SortDefault(sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestParam(required = false) Optional<String> senderAddress
  ) {
    return transactionService.findAllOnKnownSmartContracts(senderAddress, pageable);
  }

}
