package collaborate.api.user.connected;

import static collaborate.api.tag.TezosApiGatewayJobClient.ORGANIZATION_SECURE_KEY_NAME;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import collaborate.api.organization.OrganizationService;
import collaborate.api.tag.model.user.UserWalletDTO;
import collaborate.api.user.UserService;
import collaborate.api.user.model.RolesDTO;
import collaborate.api.user.model.UserDTO;
import collaborate.api.user.security.Authorizations.Roles;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class ConnectedUserService {

  private final ConnectedUserDAO connectedUserDAO;
  private final OrganizationService organizationService;
  private final UserService userService;

  public Set<String> getRealmRoles() {
    return connectedUserDAO.getRealmRoles();
  }

  public String getEmailOrThrow() {
    var emailOpt = connectedUserDAO.findEmail();
    if (emailOpt.isEmpty()) {
      var userId = connectedUserDAO.getUserId();
      throw new ResponseStatusException(
          INTERNAL_SERVER_ERROR,
          "the connected user=" + userId + " should have an email defined");
    } else {
      return emailOpt.get();
    }
  }

  public UserWalletDTO getWallet() {
    if (connectedUserDAO.isOrganization()) {
      var organization = organizationService.getCurrentOrganization();
      return UserWalletDTO.builder()
          .userId(ORGANIZATION_SECURE_KEY_NAME)
          .address(organization.getAddress())
          .build();
    } else {
      return connectedUserDAO.findEmail()
          .map(userService::findByEmailOrThrow)
          .orElseThrow(() -> new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "Connected user e-mail not found")
          );
    }
  }

  public String getWalletAddress() {
    return getWallet().getAddress();
  }

  public UserDTO updateWithAssetOwnerRole() {
    var token = connectedUserDAO.getAuthToken();
    userService.createUser(token.getEmail());
    return userService.modifyUser(token.getSubject(), new RolesDTO(Set.of(Roles.ASSET_OWNER)));
  }
}
