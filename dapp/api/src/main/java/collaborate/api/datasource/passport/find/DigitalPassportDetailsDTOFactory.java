package collaborate.api.datasource.passport.find;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.datasource.nft.model.storage.CallParams;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.organization.OrganizationService;
import collaborate.api.tag.model.Bytes;
import collaborate.api.user.UserService;
import collaborate.api.user.connected.ConnectedUserService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DigitalPassportDetailsDTOFactory {

  private final ConnectedUserService connectedUserService;
  private final OrganizationService organizationService;
  private final UserService userService;
  private final CatalogService catalogService;
  private final NftDatasourceService nftDatasourceService;
  private final ApiProperties apiProperties;
  private final TezosApiGatewayPassportClient tezosApiGatewayPassportClient;
  private final FindPassportDAO findPassportDAO;

  public List<DigitalPassportDetailsDTO> makeFromFA2(Collection<Integer> tokenIdList) {
    // Get metadata
    var tokenMetadata = nftDatasourceService.getTZip21MetadataByTokenIds(
        tokenIdList,
        apiProperties.getDigitalPassportContractAddress());

    // Get Owner addresses
    var tokenOwners = findPassportDAO.getOwnersByTokenIds(tokenIdList,
        apiProperties.getDigitalPassportContractAddress());

    // Get Operator addresses
    var tokenOperators = findPassportDAO.getOperatorsByTokenIdsAndOwners(tokenIdList, tokenOwners,
        apiProperties.getDigitalPassportContractAddress());

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

  public List<DigitalPassportDetailsDTO> makeFromMultisig(List<Integer> multisigIds,
      String ownerAddressFilter) {
    return findPassportDAO.findMultisigByIds(multisigIds).getMultisigs().stream()
        .filter(tagEntry -> tagEntry.getValue().getCallParams().getEntryPoint().equals("mint"))
        .filter(tagEntry -> !tagEntry.getValue().isOk())
        .filter(tagEntry -> ownerAddressFilter == null || ownerAddressFilter.equals(
            getOwnerAddressFromMultisig(tagEntry.getValue().getCallParams())))
        .map(tagEntry -> {
          var metadata = new Bytes("TODO");
          var ownerAddress = getOwnerAddressFromMultisig(tagEntry.getValue().getCallParams());
          var operatorAddress = getOperatorAddressFromMultisig(tagEntry.getValue().getCallParams());
          return DigitalPassportDetailsDTO.builder()
              .assetDataCatalog(null) // TODO
              .assetId(null) // TODO
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

  private String getOwnerAddressFromMultisig(CallParams callParams) {
    var parameters = (Map) (callParams.getParameters());
    var mint = (Map) ((parameters).get("mint"));
    var mint_params = (Map) ((mint).get("mint_params"));
    var address = (mint_params).get("address");
    return (String) address;
  }

  private String getOperatorAddressFromMultisig(CallParams callParams) {
    var parameters = (Map) (callParams.getParameters());
    var mint = (Map) ((parameters).get("mint"));
    var operator = (mint).get("operator");
    return (String) operator;
  }

  private Bytes getMetadataFromMultisig(CallParams callParams) {
    var parameters = (Map) (callParams.getParameters());
    var mint = (Map) ((parameters).get("mint"));
    var mint_params = (Map) ((mint).get("mint_params"));
    var address = (Map) ((mint_params).get("metadata"));
    var metadataIpfs = (address).get("");
    return (Bytes) metadataIpfs; ///////// BytesDeserializer
  }

}
