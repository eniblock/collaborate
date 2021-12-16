package collaborate.api.user.providers;

import collaborate.api.config.security.KeycloakAdminClientProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.IdentityProviderMapperRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentityProviderService {

  private final Keycloak keycloak;
  private final KeycloakAdminClientProperties keycloakAdminClientProperties;

  public void create(IdentityProviderRepresentation identityConfig) {
    try (var response = keycloak.realm(keycloakAdminClientProperties.getRealm())
        .identityProviders()
        .create(identityConfig)) {
      log.info("Create identity-provider(alias={}), result status={}",
          identityConfig.getAlias(),
          response.getStatus());
    }
  }

  public void createMapper(IdentityProviderMapperRepresentation mapper) {
    try (var response = keycloak.realm(keycloakAdminClientProperties.getRealm())
        .identityProviders()
        .get(mapper.getIdentityProviderAlias())
        .addMapper(mapper)) {
      log.info("Create mapper(name={}) on identity-provider(alias={}), result status={}",
          mapper.getName(),
          mapper.getIdentityProviderAlias(),
          response.getStatus());
    }
  }

  public List<IdentityProviderMapperRepresentation> listMapper(String providerAlias) {
    return keycloak.realm(keycloakAdminClientProperties.getRealm())
        .identityProviders()
        .get(providerAlias)
        .getMappers();
  }

  public void deleteMapper(String providerAlias, String mapperId) {
    keycloak.realm(keycloakAdminClientProperties.getRealm())
        .identityProviders()
        .get(providerAlias)
        .delete(mapperId);
  }

  public List<IdentityProviderRepresentation> findAll() {
    return keycloak.realm(keycloakAdminClientProperties.getRealm())
        .identityProviders()
        .findAll();
  }

  public void deleteIdentityProvider(String providerAlias) {
    keycloak.realm(keycloakAdminClientProperties.getRealm())
        .identityProviders()
        .get(providerAlias)
        .remove();
  }
}
