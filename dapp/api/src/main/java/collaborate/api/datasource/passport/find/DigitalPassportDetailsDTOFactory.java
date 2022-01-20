package collaborate.api.datasource.passport.find;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.datasource.passport.model.TokenStatus;
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
public class DigitalPassportDetailsDTOFactory {

  private final ApiProperties apiProperties;
  private final CatalogService catalogService;
  private final ConnectedUserService connectedUserService;
  private final FindPassportDAO findPassportDAO;
  private final IpfsService ipfsService;
  private final NftDatasourceService nftDatasourceService;
  private final OrganizationService organizationService;
  private final TezosApiGatewayPassportClient tezosApiGatewayPassportClient;
  private final UserService userService;

  public List<DigitalPassportDetailsDTO> makeFromFA2(Collection<Integer> tokenIdList) {
    // Get metadata
    var tokenMetadata = nftDatasourceService.getTZip21MetadataByTokenIds(
        tokenIdList,
        apiProperties.getDigitalPassportContractAddress());

    // Get Owner addresses
    var tokenOwners = findPassportDAO.getOwnersByTokenIds(tokenIdList);

    // Get Operator addresses
    var tokenOperators = findPassportDAO.getOperatorsByTokenIdsAndOwners(tokenIdList, tokenOwners);

    return tokenIdList.stream()
        .map(tokenId -> {
              var metadata = tokenMetadata.get(tokenId);
              var owner = tokenOwners.get(tokenId);
              var operator = tokenOperators.get(tokenId);
              return DigitalPassportDetailsDTO.builder()
                  .assetDataCatalog(catalogService.getAssetDataCatalogDTO(metadata)
                      .orElse(null))
                  .assetId(metadata == null ? null : metadata.getAssetId().orElse(null))
                  .assetOwner(userService.getByWalletAddress(owner))
                  .accessStatus(makeAccessStatus(owner, operator))
                  .creationDatetime(null)
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

  public List<DigitalPassportDetailsDTO> makeFromMultiSig(
      List<Integer> multiSigIdList
  ) {
    return findPassportDAO.findMultisigByIds(multiSigIdList).getMultisigs().stream()
        .map(tagEntry -> {
          var metadataIpfsUri = tagEntry.getValue().getCallParams().getMetadataFromMultisig();
          var metadata = ipfsService.cat(
              metadataIpfsUri.toString(), TZip21Metadata.class);
          var ownerAddress = tagEntry.getValue().getCallParams().getOwnerAddressFromMultisig();
          var operatorAddress = tagEntry.getValue().getCallParams()
              .getOperatorAddressFromMultisig();
          return DigitalPassportDetailsDTO.builder()
              .assetDataCatalog(catalogService.getAssetDataCatalogDTO(metadata)
                  .orElse(null))
              .assetId(metadata == null ? null : metadata.getAssetId().orElse(null))
              .assetOwner(userService.getByWalletAddress(ownerAddress))
              .accessStatus(AccessStatus.PENDING)
              .creationDatetime(null)
              .operator(organizationService.getByWalletAddress(operatorAddress))
              .multisigContractId(tagEntry.getKey())
              .tokenId(null)
              .tokenStatus(TokenStatus.PENDING_CREATION)
              .build();
        })
        .collect(Collectors.toList());
  }

}
