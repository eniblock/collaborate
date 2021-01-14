package collaborate.api.config;

import collaborate.api.services.UserService;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
public class SuperAdminConfiguration {

    @Autowired
    public UserService userService;

    @Autowired
    public Keycloak keycloak;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        userService.retriableKeycloak();
    }

}
