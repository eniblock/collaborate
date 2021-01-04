package collaborate.login_event_listener;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class SendEmailEventListenerProviderFactory implements EventListenerProviderFactory {

    @Override
    public SendEmailEventListenerProvider create(KeycloakSession keycloakSession) {
        return new SendEmailEventListenerProvider(keycloakSession);
    }

    @Override
    public void init(Config.Scope scope) {
        //
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        //
    }

    @Override
    public void close() {
        //
    }

    @Override
    public String getId() {
        return "send_email_after_register";
    }

}