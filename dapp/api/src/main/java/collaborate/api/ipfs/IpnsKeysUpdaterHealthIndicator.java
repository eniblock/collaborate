package collaborate.api.ipfs;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.health.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Setter
@ConditionalOnEnabledHealthIndicator("ipns-updater")
public class IpnsKeysUpdaterHealthIndicator implements HealthIndicator {

  private Status status = Status.UNKNOWN;

  @Override
  public Health health() {
    return Health.status(status).build();
  }
}
