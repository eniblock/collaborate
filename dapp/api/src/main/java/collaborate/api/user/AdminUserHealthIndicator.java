package collaborate.api.user;

import feign.RetryableException;
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
public class AdminUserHealthIndicator implements HealthIndicator {

  private final UserService userService;

  @Override
  public Health health() {
    var health = Health.unknown();
    try{
      var adminUser = userService.getAdminUser();

      health = Health.up()
          .withDetails(Map.of("wallet", adminUser.getAddress(),
              "user", adminUser.getUserId())
          );
    } catch(RetryableException ex){
      health = Health.down().withException(ex);
    }

    return health.build();
  }
}
