package collaborate.api.datasource.passport.find;

import static collaborate.api.user.security.Authorizations.Roles.ASSET_OWNER;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.multisig.ProxyTokenControllerTransactionService;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
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

  private final ApiProperties apiProperties;
  private final DigitalPassportDetailsDTOFactory digitalPassportDetailsDTOFactory;
  private final ConnectedUserService connectedUserService;
  private final FindPassportDAO findPassportDAO;
  private final ProxyTokenControllerTransactionService proxyTokenControllerTransactionService;
  private final UserService userService;

  public List<DigitalPassportDetailsDTO> findPassportDetailsFromMultisig(String ownerAddress) {
    var multiSigIdList = proxyTokenControllerTransactionService.findMultiSigIdListByOwner(
        apiProperties.getDigitalPassportProxyTokenControllerContractAddress(),
        ownerAddress
    );
    return findPassportDetailsFromMultisigIdList(multiSigIdList);
  }

  public List<DigitalPassportDetailsDTO> findPassportDetailsFromMultisigByOperator(
      String operatorAddress) {
    var multiSigIdList = proxyTokenControllerTransactionService.findMultiSigIdListByOperator(
        apiProperties.getDigitalPassportProxyTokenControllerContractAddress(),
        operatorAddress
    );
    return findPassportDetailsFromMultisigIdList(multiSigIdList);
  }

  public Optional<DigitalPassportDetailsDTO> findPassportDetailsFromMultisigId(Integer contractId) {
    var l = findPassportDetailsFromMultisigIdList(List.of(contractId));
    return (l == null || l.isEmpty())
        ? Optional.empty()
        : Optional.of(l.get(0));
  }

  private List<DigitalPassportDetailsDTO> findPassportDetailsFromMultisigIdList(
      List<Integer> multisigIds) {
    return digitalPassportDetailsDTOFactory.makeFromMultiSig(multisigIds);
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
      var connectedUserWallet = connectedUserService.getWallet();
      var tokenIds = findAllTokenIds();
      digitalPassports = findPassportDetailsByTokenIdList(tokenIds);
      digitalPassports.addAll(
          findPassportDetailsFromMultisigByOperator(connectedUserWallet.getAddress()));
    }

    return digitalPassports;
  }

  // FIXME remove for loop
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
