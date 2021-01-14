package xdev.keycloak.api.resource;

import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.AccessToken;

public class AdminAuth extends org.keycloak.services.resources.admin.AdminAuth {
    public AdminAuth(RealmModel realm, AccessToken token, UserModel user, ClientModel client) {
        super(realm, token, user, client);
    }
}
