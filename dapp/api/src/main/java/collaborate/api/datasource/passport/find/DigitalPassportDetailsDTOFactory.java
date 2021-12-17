package collaborate.api.datasource.passport.find;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.nft.catalog.CatalogService;
import collaborate.api.datasource.nft.catalog.NftDatasourceService;
import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
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

  public List<DigitalPassportDetailsDTO> makeFromFA2(Collection<Integer> tokenIdList) {
    // Get metadata from tokenIdList
    Map<Integer, TZip21Metadata> tokenMetadata = new HashMap<>();
    tokenIdList.stream()
        .forEach(tokenId -> tokenMetadata.put(
            tokenId,
            nftDatasourceService.getTZip21MetadataByTokenId(tokenId,
                apiProperties.getDigitalPassportContractAddress()).orElse(null))
        );

    // Get Owner addresses
    Map<Integer, String> tokenOwner = new HashMap<>();
    var requestOwner = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.OWNER_BY_TOKEN_ID, tokenIdList)
    ));
    tezosApiGatewayPassportClient.getOwnersByTokenIds(
            apiProperties.getDigitalPassportContractAddress(), requestOwner)
        .getOwnerBuTokenId().stream()
        .forEach(tagEntry -> tokenOwner.put(
            tagEntry.getKey(),
            tagEntry.getValue())
        );

    // Get Operator addresses
    Map<Integer, String> tokenOperator = new HashMap<>();
    var requestOperator = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.OPERATORS_BY_TOKEN,
            tokenIdList.stream()
                .map(tokenId -> new TagPair<String, Integer>(tokenOwner.get(tokenId), tokenId))
                .collect(Collectors.toList())
        )
    ));
    tezosApiGatewayPassportClient.getOperatorsByTokenIdsAndOwners(
            apiProperties.getDigitalPassportContractAddress(), requestOperator)
        .getOperatorsByToken().stream()
        .forEach(tagEntry -> tokenOperator.put(
            tagEntry.getKey().getY(),
            tagEntry.getValue().get(0))
        );

    return tokenIdList.stream()
        .map(tokenId -> {
              var metadata = tokenMetadata.get(tokenId);
              var owner = tokenOwner.get(tokenId);
              var operator = tokenOperator.get(tokenId);
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
