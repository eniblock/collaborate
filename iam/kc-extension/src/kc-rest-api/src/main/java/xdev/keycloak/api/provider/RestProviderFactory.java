package xdev.keycloak.api.provider;

import javax.persistence.EntityManager;

import org.keycloak.Config;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class RestProviderFactory implements RealmResourceProviderFactory {

    public static final String ID = "custom-api";

    @Override
    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        EntityManager em = keycloakSession.getProvider(JpaConnectionProvider.class).getEntityManager();
        return new RestProvider(keycloakSession, em);
    }

    @Override
    public void init(Config.Scope scope) {

    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return ID;
    }
}
