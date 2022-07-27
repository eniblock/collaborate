package collaborate.api.organization;

import collaborate.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitAdminWalletOnApplicationStartup {

  private final UserService userService;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    userService.ensureAdminWalletExists();
  }
}
