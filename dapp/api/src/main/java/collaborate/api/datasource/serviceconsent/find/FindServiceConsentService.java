package collaborate.api.datasource.serviceconsent.find;

import static collaborate.api.user.security.Authorizations.Roles.ASSET_OWNER;

import collaborate.api.datasource.multisig.ProxyTokenControllerTransactionService;
import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransaction;
import collaborate.api.datasource.serviceconsent.model.ServiceConsentDetailsDTO;
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
public class FindServiceConsentService {

  private final String serviceConsentProxyControllerContractAddress;
  private final ServiceConsentDetailsDTOFactory serviceConsentDetailsDTOFactory;
  private final ConnectedUserService connectedUserService;
  private final FindServiceConsentDAO findServiceConsentDAO;
  private final ProxyTokenControllerTransactionService proxyTokenControllerTransactionService;
  private final UserService userService;

  public List<ServiceConsentDetailsDTO> findServiceConsentDetailsFromMultisigByOwner(
      String ownerAddress) {
    var transactionList = proxyTokenControllerTransactionService
        .findMultiSigListTransactionByOwner(
            serviceConsentProxyControllerContractAddress,
            ownerAddress
        );
    return findServiceConsentDetailsFromMultisigTransaction(transactionList);
  }

  public List<ServiceConsentDetailsDTO> findServiceConsentDetailsFromMultisigByOperator(
      String operatorAddress) {
    var transactionList = proxyTokenControllerTransactionService.findMultiSigListTransactionByOperator(
        serviceConsentProxyControllerContractAddress,
        operatorAddress
    );
    return findServiceConsentDetailsFromMultisigTransaction(transactionList);
  }

  public Optional<ServiceConsentDetailsDTO> findServiceConsentDetailsFromMultisigId(Integer contractId) {
    var transaction = proxyTokenControllerTransactionService.getTransactionByTokenId(
        serviceConsentProxyControllerContractAddress,
        Long.valueOf(contractId)
    );

    var serviceConsentsDetails = findServiceConsentDetailsFromMultisigTransaction(List.of(transaction));
    return Optional.ofNullable(serviceConsentsDetails)
        .flatMap(l -> l.stream().findFirst());
  }


  private List<ServiceConsentDetailsDTO> findServiceConsentDetailsFromMultisigTransaction(
      List<ProxyTokenControllerTransaction> transactionList
  ) {
    return serviceConsentDetailsDTOFactory.makeFromMultiSig(transactionList);
  }

  public List<ServiceConsentDetailsDTO> findServiceConsentDetailsByTokenIdList(
      Collection<Integer> tokenIdList) {
    return serviceConsentDetailsDTOFactory.makeFromFA2(tokenIdList);
  }

  public Optional<ServiceConsentDetailsDTO> findServiceConsentDetailsByTokenId(Integer tokenId) {
    var serviceConsentsDetails = findServiceConsentDetailsByTokenIdList(List.of(tokenId));
    return Optional.ofNullable(serviceConsentsDetails)
        .flatMap(l -> l.stream().findFirst());
  }

  public Collection<ServiceConsentDetailsDTO> getByConnectedUser() {
    Collection<ServiceConsentDetailsDTO> serviceConsents;

    Set<String> roles = connectedUserService.getRealmRoles();
    if (roles.contains(ASSET_OWNER)) {
      var connectedUserEmail = connectedUserService.getEmailOrThrow();
      var connectedUserWallet = userService.findWalletAddressByEmailOrThrow(connectedUserEmail);
      var tokenIds = findServiceConsentDAO.getTokenIdsByOwner(connectedUserWallet);
      serviceConsents = findServiceConsentDetailsByTokenIdList(tokenIds);
      serviceConsents.addAll(findServiceConsentDetailsFromMultisigByOwner(connectedUserWallet));
    } else {
      var connectedUserWallet = connectedUserService.getWallet();
      var tokenIds = findAllTokenIds();
      serviceConsents = findServiceConsentDetailsByTokenIdList(tokenIds);
      serviceConsents.addAll(
          findServiceConsentDetailsFromMultisigByOperator(connectedUserWallet.getAddress()));
    }

    return serviceConsents;
  }

  // FIXME remove for loop
  private Collection<Integer> findAllTokenIds() {
    var allTokens = findServiceConsentDAO.countServiceConsent();
    var tokenIds = new LinkedList<Integer>();
    for (int i = 0; i < allTokens; i++) {
      tokenIds.add(i);
    }
    return tokenIds;
  }

  public long countServiceConsent() {
    // FIXME: the serviceconsent count should done using the ProxyTokenControllerTransactionDAO to count the signed NFT on the Digital serviceconsent FA2 SmartContract
    return findServiceConsentDAO.countServiceConsent();
  }

}
