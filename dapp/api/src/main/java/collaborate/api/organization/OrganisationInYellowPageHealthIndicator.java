package collaborate.api.organization;

import collaborate.api.user.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Setter
@ConditionalOnEnabledHealthIndicator("organization")
public class OrganisationInYellowPageHealthIndicator implements HealthIndicator {

  private final OrganizationService organizationService;
  private final UserService userService;
  private final String organizationYellowPageContractAddress;

  @Override
  public Health health() {
    var health = Health.unknown();
    try {
      var walletAddress = userService.getAdminUser().getAddress();
      health = organizationService.findOrganizationByPublicKeyHash(
          walletAddress,
          organizationYellowPageContractAddress
      ).map(o -> Health.up()
          .withDetails(Map.of(
              "contract", organizationYellowPageContractAddress,
              "roles", o.getRoles(),
              "legal-name", o.getLegalName()
          ))
      ).orElse(
          Health.down()
              .withDetails(Map.of("contract", organizationYellowPageContractAddress))
      );
    } catch (Exception ex) {
      health = Health.unknown().withException(ex);
    }

    return health.build();
  }
}
