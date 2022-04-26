package collaborate.api.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class ReadinessStateHealthIndicator implements HealthIndicator {

  @Override
  public Health health() {
    return Health.up().build();
  }
}
