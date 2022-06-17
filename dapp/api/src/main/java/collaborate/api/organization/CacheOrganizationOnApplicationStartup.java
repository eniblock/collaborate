package collaborate.api.organization;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheOrganizationOnApplicationStartup {

  private final OrganizationService organizationService;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    organizationService.getAllOrganizations();
  }
}