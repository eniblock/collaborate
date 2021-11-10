package collaborate.api.user.connected;

import static collaborate.api.user.security.Authorizations.Roles.getOrganizationRoles;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.util.CollectionUtils.containsAny;

import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@Repository
class ConnectedUserDAO {

  /**
   * @return The connected user token, when not found a 403 Forbidden exception is thrown
   */
  public AccessToken getAuthToken() {
    Optional<AccessToken> accessTokenOptResult = Optional.empty();
    var rawPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (rawPrincipal instanceof KeycloakPrincipal) {
      KeycloakPrincipal<?> principal = (KeycloakPrincipal<?>) rawPrincipal;
      var session = principal.getKeycloakSecurityContext();
      accessTokenOptResult = Optional.of(session.getToken());
    }
    return accessTokenOptResult
        .orElseThrow(() -> new ResponseStatusException(FORBIDDEN, "No token"));
  }

  public Optional<String> findEmail() {
    return Optional.of(getAuthToken().getEmail());
  }

  public Set<String> getRealmRoles() {
    return getAuthToken()
        .getRealmAccess()
        .getRoles();
  }

  public String getUserId() {
    return getAuthToken().getSubject();
  }

  public boolean isOrganization() {
    return containsAny(getOrganizationRoles(), getRealmRoles());
  }
}
