package collaborate.api.user.security;

import static collaborate.api.user.security.Authorizations.Roles.ORGANIZATION_ROLES;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
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
public class ConnectedUserDAO {

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

  public Optional<String> getEmail() {
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

  public String getEmailOrThrow() {
    var emailOpt = getEmail();
    if (emailOpt.isEmpty()) {
      var userId = getUserId();
      throw new ResponseStatusException(
          INTERNAL_SERVER_ERROR,
          "the connected user=" + userId + " should have an email defined");
    } else {
      return emailOpt.get();
    }
  }

  public boolean isOrganization() {
    return containsAny(ORGANIZATION_ROLES, getRealmRoles());
  }
}
