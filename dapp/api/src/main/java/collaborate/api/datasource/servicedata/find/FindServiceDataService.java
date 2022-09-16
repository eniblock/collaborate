package collaborate.api.datasource.servicedata.find;

import static collaborate.api.user.security.Authorizations.Roles.ASSET_OWNER;

import collaborate.api.datasource.multisig.ProxyTokenControllerTransactionService;
import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransaction;
import collaborate.api.datasource.servicedata.model.ServiceDataDetailsDTO;
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
public class FindServiceDataService {

  private final String serviceDataProxyControllerContractAddress;
  private final ServiceDataDetailsDTOFactory serviceDataDetailsDTOFactory;
  private final ConnectedUserService connectedUserService;
  private final FindServiceDataDAO findServiceDataDAO;
  private final ProxyTokenControllerTransactionService proxyTokenControllerTransactionService;
  private final UserService userService;

  public List<ServiceDataDetailsDTO> findServiceDataDetailsFromMultisigByOwner(
      String ownerAddress) {
    var transactionList = proxyTokenControllerTransactionService
        .findMultiSigListTransactionByOwner(
            serviceDataProxyControllerContractAddress,
            ownerAddress
        );
    return findServiceDataDetailsFromMultisigTransaction(transactionList);
  }

  public List<ServiceDataDetailsDTO> findServiceDataDetailsFromMultisigByOperator(
      String operatorAddress) {
    var transactionList = proxyTokenControllerTransactionService.findMultiSigListTransactionByOperator(
        serviceDataProxyControllerContractAddress,
        operatorAddress
    );
    return findServiceDataDetailsFromMultisigTransaction(transactionList);
  }

  public Optional<ServiceDataDetailsDTO> findServiceDataDetailsFromMultisigId(Integer contractId) {
    var transaction = proxyTokenControllerTransactionService.getTransactionByTokenId(
        serviceDataProxyControllerContractAddress,
        Long.valueOf(contractId)
    );

    var serviceDatasDetails = findServiceDataDetailsFromMultisigTransaction(List.of(transaction));
    return Optional.ofNullable(serviceDatasDetails)
        .flatMap(l -> l.stream().findFirst());
  }


  private List<ServiceDataDetailsDTO> findServiceDataDetailsFromMultisigTransaction(
      List<ProxyTokenControllerTransaction> transactionList
  ) {
    return serviceDataDetailsDTOFactory.makeFromMultiSig(transactionList);
  }

  public List<ServiceDataDetailsDTO> findServiceDataDetailsByTokenIdList(
      Collection<Integer> tokenIdList) {
    return serviceDataDetailsDTOFactory.makeFromFA2(tokenIdList);
  }

  public Optional<ServiceDataDetailsDTO> findServiceDataDetailsByTokenId(Integer tokenId) {
    var serviceDatasDetails = findServiceDataDetailsByTokenIdList(List.of(tokenId));
    return Optional.ofNullable(serviceDatasDetails)
        .flatMap(l -> l.stream().findFirst());
  }

  public Collection<ServiceDataDetailsDTO> getByConnectedUser() {
    Collection<ServiceDataDetailsDTO> serviceDatas;

    Set<String> roles = connectedUserService.getRealmRoles();
    if (roles.contains(ASSET_OWNER)) {
      var connectedUserEmail = connectedUserService.getEmailOrThrow();
      var connectedUserWallet = userService.findWalletAddressByEmailOrThrow(connectedUserEmail);
      var tokenIds = findServiceDataDAO.getTokenIdsByOwner(connectedUserWallet);
      serviceDatas = findServiceDataDetailsByTokenIdList(tokenIds);
      serviceDatas.addAll(findServiceDataDetailsFromMultisigByOwner(connectedUserWallet));
    } else {
      var connectedUserWallet = connectedUserService.getWallet();
      var tokenIds = findAllTokenIds();
      serviceDatas = findServiceDataDetailsByTokenIdList(tokenIds);
      serviceDatas.addAll(
          findServiceDataDetailsFromMultisigByOperator(connectedUserWallet.getAddress()));
    }

    return serviceDatas;
  }

  // FIXME remove for loop
  private Collection<Integer> findAllTokenIds() {
    var allTokens = findServiceDataDAO.countServiceData();
    var tokenIds = new LinkedList<Integer>();
    for (int i = 0; i < allTokens; i++) {
      tokenIds.add(i);
    }
    return tokenIds;
  }

  public long countServiceData() {
    // FIXME: the servicedata count should done using the ProxyTokenControllerTransactionDAO to count the signed NFT on the Digital servicedata FA2 SmartContract
    return findServiceDataDAO.countServiceData();
  }

}
