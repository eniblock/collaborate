package collaborate.api.datasource.passport.find;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.datasource.passport.model.AccessStatus;
import collaborate.api.datasource.passport.model.DigitalPassportDetailsDTO;
import collaborate.api.datasource.passport.model.TokenStatus;
import collaborate.api.datasource.passport.model.storage.StorageFields;
import collaborate.api.organization.OrganizationService;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import collaborate.api.tag.model.storage.TagPair;
import collaborate.api.user.UserService;
import collaborate.api.user.connected.ConnectedUserService;
import java.util.Collection;
import java.util.HashMap;
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
                  .assetId(metadata.getAssetId().orElse(null))
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

  private AccessStatus makeAccessStatus(String nftOwnerAddress, String operatorAddress) {
    var connectedUserWalletAddress = connectedUserService.getWalletAddress();
    if (connectedUserWalletAddress.equals(nftOwnerAddress) || connectedUserWalletAddress.equals(
        operatorAddress)) {
      return AccessStatus.GRANTED;
    }
    return AccessStatus.LOCKED;
  }

}
