package collaborate.api.tag;

import feign.RetryableException;
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
@ConditionalOnEnabledHealthIndicator("tag")
public class TagHealthIndicator implements HealthIndicator {

  private final TezosApiGatewayConfClient tezosApiGatewayConfClient;

  @Override
  public Health health() {
    var health = Health.unknown();
    try{
      health = Health.up()
          .withDetails(tezosApiGatewayConfClient.getConfiguration());
    } catch(RetryableException ex){
      health = Health.down().withException(ex);
    }

    return health.build();
  }
}
