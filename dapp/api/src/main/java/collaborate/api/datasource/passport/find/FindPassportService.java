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
import org.springframework.lang.Nullable;
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
      @Nullable String ownerAddress) {
    var multisigNb = findPassportDAO.countMultisigs();
    var multisigIds = new LinkedList<Integer>();
    for (int i = 0; i < multisigNb; i++) {
      multisigIds.add(i);
    }
    return findPassportDetailsFromMultisigIdList(multisigIds, ownerAddress);
  }

  public Optional<DigitalPassportDetailsDTO> findPassportDetailsFromMultisigId(Integer contractId) {
    var l = findPassportDetailsFromMultisigIdList(List.of(contractId), null);
    return (l == null || l.isEmpty())
        ? Optional.empty()
        : Optional.of(l.get(0));
  }

  private List<DigitalPassportDetailsDTO> findPassportDetailsFromMultisigIdList(
      List<Integer> multisigIds, String ownerAddress) {

    // TODO

    // 1) on charge tous les multisigs
    // 2) filtre sur entry_point="mint"
    // 3) filtre sur le owner
    // 4) filtre sur ok=false

    // 5) Récupérer  l'adresse ipfs, @alice, @dsp
    // 6) récupérer les info ipfs
    // 7) nourrir le DTO

    return List.of();
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
    var allTokens = findPassportDAO.countPassports();
    var tokenIds = new LinkedList<Integer>();
    for (int i = 0; i < allTokens; i++) {
      tokenIds.add(i);
    }
    return tokenIds;
  }

  public long countPassports() {
    return findPassportDAO.countPassports();
  }

}
