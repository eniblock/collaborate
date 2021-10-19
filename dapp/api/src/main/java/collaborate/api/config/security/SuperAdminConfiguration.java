package collaborate.api.config.security;

import collaborate.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
public class SuperAdminConfiguration {

  private final UserService userService;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    userService.retriableKeycloak();
  }

}
