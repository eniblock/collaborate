package xdev.keycloak.api.provider;

import javax.persistence.EntityManager;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.resource.RealmResourceProvider;

import xdev.keycloak.api.resource.UserResource;

public class RestProvider implements RealmResourceProvider {

    private final KeycloakSession session;
    private final EntityManager em;

    public RestProvider(KeycloakSession session, EntityManager em) {
        this.session = session;
        this.em = em;
    }

    @Override
    public Object getResource() {

        RealmModel realm = session.getContext().getRealm();
        UserResource user = new UserResource(realm, em);
        ResteasyProviderFactory.getInstance().injectProperties(user);
        user.setup();
        return user;

    }


    @Override
    public void close() {

    }
}
