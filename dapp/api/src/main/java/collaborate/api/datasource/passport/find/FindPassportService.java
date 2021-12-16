package collaborate.api.datasource.passport.find;

import static collaborate.api.user.security.Authorizations.Roles.ASSET_OWNER;

import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.organization.OrganizationService;
import collaborate.api.user.UserService;
import collaborate.api.user.connected.ConnectedUserService;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FindPassportService {

  private final DigitalPassportDetailsDTOFactory digitalPassportDetailsDTOFactory;
  private final ConnectedUserService connectedUserService;
  private final FindPassportDAO findPassportDAO;
  private final OrganizationService organizationService;
  private final UserService userService;

  public List<DigitalPassportDetailsDTO> findPassportDetailsFromMultisig(
      String ownerAddress) {

    // !!! onwerAddress peut etre null => pas de filtre sur le owner dans ce cas
    // 1) on charge tous les multisigs
    // 2) filtre sur entry_point="mint"
    // 3) filtre sur le owner
    // 4) filtre sur ok=false

    // 1) récupérer dans les multisig du Proxy (filtrer : ok=false ET callParam="mint")
    // 2) Récupérer  l'adresse ipfs, @alice, @dsp
    // 3) récupérer les info ipfs
    // 4) nourrir le DTO

    return new LinkedList<>();
  }

  public Optional<DigitalPassportDetailsDTO> findPassportDetailsFromMultisigId(Integer contractId) {
    // TODO
    return Optional.empty();
  }

  public List<DigitalPassportDetailsDTO> findPassportDetailsByTokenIdList(
      Collection<Integer> tokenIdList) {
    return digitalPassportDetailsDTOFactory.makeFromFA2(tokenIdList);
  }

  public Optional<DigitalPassportDetailsDTO> findPassportDetailsByTokenId(Integer tokenId) {
    var l = findPassportDetailsByTokenIdList(List.of(tokenId));
    return (l == null || l.isEmpty())
        ? Optional.empty()
        : Optional.of(l.get(0));
  }

  public Collection<DigitalPassportDetailsDTO> getByConnectedUser() {
    Collection<DigitalPassportDetailsDTO> digitalPassports;

    Set<String> roles = connectedUserService.getRealmRoles();
    if (roles.contains(ASSET_OWNER)) {
      var connectedUserEmail = connectedUserService.getEmailOrThrow();
      var connectedUserWallet = userService.findWalletAddressByEmailOrThrow(connectedUserEmail);
      var tokenIds = findPassportDAO.getTokenIdsByOwner(connectedUserWallet);
      digitalPassports = findPassportDetailsByTokenIdList(tokenIds);
      digitalPassports.addAll(findPassportDetailsFromMultisig(connectedUserWallet));
    } else {
      var tokenIds = findAllTokenIds();
      digitalPassports = findPassportDetailsByTokenIdList(tokenIds);
      digitalPassports.addAll(findPassportDetailsFromMultisig(null));
    }

    return digitalPassports;
  }

  private Collection<Integer> findAllTokenIds() {
    var allTokens = findPassportDAO.count();
    var tokenIds = new LinkedList<Integer>();
    for (int i = 0; i < allTokens; i++) {
      tokenIds.add(i);
    }
    return tokenIds;
  }

  public long count() {
    return findPassportDAO.count();
  }


}
