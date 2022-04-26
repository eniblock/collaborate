package collaborate.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@RequiredArgsConstructor
public class ContextInitFinishListener implements ApplicationListener<ContextRefreshedEvent> {

  private final ApplicationEventPublisher eventPublisher;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    // When bean definitions are initialized, Then Changing the liveness state to CORRECT
    AvailabilityChangeEvent.publish(eventPublisher, this, LivenessState.CORRECT);
  }
}
