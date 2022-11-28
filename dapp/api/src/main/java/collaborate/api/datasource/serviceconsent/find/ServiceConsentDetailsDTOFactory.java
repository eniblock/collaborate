package collaborate.api.datasource.serviceconsent.find;

import collaborate.api.datasource.multisig.model.ProxyTokenControllerTransaction;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.datasource.serviceconsent.model.AccessStatus;
import collaborate.api.datasource.serviceconsent.model.ServiceConsentDetailsDTO;
import collaborate.api.datasource.serviceconsent.model.TokenStatus;
import collaborate.api.datasource.serviceconsent.transaction.ServiceConsentTransactionService;
import collaborate.api.ipfs.IpfsService;
import collaborate.api.organization.OrganizationService;
import collaborate.api.user.UserService;
import collaborate.api.user.connected.ConnectedUserService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ServiceConsentDetailsDTOFactory {

  private final String serviceConsentContractAddress;
  private final CatalogService catalogService;
  private final ConnectedUserService connectedUserService;
  private final FindServiceConsentDAO findServiceConsentDAO;
  private final IpfsService ipfsService;
  private final NftDatasourceService nftDatasourceService;
  private final OrganizationService organizationService;
  private final ServiceConsentTransactionService serviceConsentTransactionService;
  private final UserService userService;

  public List<ServiceConsentDetailsDTO> makeFromFA2(Collection<Integer> tokenIdList) {
    /**
     * FIXME:
     * - 3 SC requests are done to build the DTO however TAG enable to get these data with a single storage query
     * - Another improvement could also be to get the information from the DB as a cached value
     */
    var tokenMetadataByTokenId = nftDatasourceService.getTZip21MetadataByTokenIds(
        tokenIdList,
        serviceConsentContractAddress);

    var tokenOwnersByTokenId = findServiceConsentDAO.getOwnersByTokenIds(tokenIdList);

    var tokenOperatorsByTokenId = findServiceConsentDAO.getOperatorsByTokenIdsAndOwners(tokenIdList,
        tokenOwnersByTokenId);

    return tokenIdList.stream()
        .map(tokenId -> {
              var metadata = tokenMetadataByTokenId.get(tokenId);
              var owner = tokenOwnersByTokenId.get(tokenId);
              var operator = tokenOperatorsByTokenId.get(tokenId);
              var creationDatetime =
                  serviceConsentTransactionService
                      .findTransactionDateByTokenId(
                          serviceConsentContractAddress,
                          Long.valueOf(tokenId)
                      );

            if (metadata != null)
            log.debug(metadata.toString()+" "+metadata.getAssetId());
            
              return ServiceConsentDetailsDTO.builder()
                  //.assetDataCatalog(catalogService.getAssetDataCatalogDTO(metadata).orElse(null))
                  .assetId(metadata == null ? null : metadata.getAssetId().orElse(null))
                  .assetOwner(userService.getByWalletAddress(owner))
                  //.accessStatus(makeAccessStatus(owner, operator))
                  .creationDatetime(creationDatetime.orElse(null))
                  .operator(organizationService.getByWalletAddress(operator))
                  .multisigContractId(null)
                  .tokenId(tokenId)
                  .tokenStatus(TokenStatus.CREATED)
                  .build();
            }
        )
        .collect(Collectors.toList());
  }

  AccessStatus makeAccessStatus(String nftOwnerAddress, String operatorAddress) {
    var connectedUserWalletAddress = connectedUserService.getWalletAddress();
    if (connectedUserWalletAddress.equals(nftOwnerAddress) || connectedUserWalletAddress.equals(
        operatorAddress)) {
      return AccessStatus.GRANTED;
    }
    return AccessStatus.LOCKED;
  }

  public List<ServiceConsentDetailsDTO> makeFromMultiSig(
      List<ProxyTokenControllerTransaction> transactionList
  ) {
    return transactionList.stream()
        .map(transaction -> {
              var multisigContractId = transaction.getMultiSigId();
              var operatorAddress = transaction.getOperator();
              var ownerAddress = transaction.getOwner();
              var metadataIpfsUri = transaction.getMetadata();
              var metadata = ipfsService.cat(
                  metadataIpfsUri, TZip21Metadata.class);
              return ServiceConsentDetailsDTO.builder()
                  //.assetDataCatalog(catalogService.getAssetDataCatalogDTO(metadata).orElse(null))
                  .assetId(metadata == null ? null : metadata.getAssetId().orElse(null))
                  .assetOwner(userService.getByWalletAddress(ownerAddress))
                  //.accessStatus(AccessStatus.PENDING)
                  .creationDatetime(null)
                  .operator(organizationService.getByWalletAddress(operatorAddress))
                  .multisigContractId(Math.toIntExact(multisigContractId))
                  .tokenId(null)
                  .tokenStatus(TokenStatus.PENDING_CREATION)
                  .build();
            }
        ).collect(Collectors.toList());
  }

}
