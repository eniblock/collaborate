package collaborate.api.datasource.passport.find;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.datasource.nft.model.storage.TokenMetadata;
import collaborate.api.datasource.passport.model.storage.StorageFields;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import collaborate.api.tag.model.storage.TagPair;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class FindPassportDAO {

  private final String digitalPassportContractAddress;
  private final String digitalPassportProxyControllerContractAddress;
  private final TezosApiGatewayPassportClient tezosApiGatewayPassportClient;

  public MultisigTagResponseDTO findMultisigByIds(Collection<Integer> multisigIds) {
    var requestMultisigs = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.MULTISIGS, multisigIds)
    ));
    return tezosApiGatewayPassportClient
        .getMultisigs(
            digitalPassportProxyControllerContractAddress,
            requestMultisigs
        );
  }

  public List<TagEntry<Integer, TokenMetadata>> findTokenMetadataByTokenIds(
      Collection<Integer> tokenIds) {
    var requestTokenMetadata = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.TOKEN_METADATA, tokenIds)
    ));
    return tezosApiGatewayPassportClient
        .getTokenMetadata(
            digitalPassportContractAddress,
            requestTokenMetadata
        ).getTokenMetadata();
  }

  public Optional<TokenMetadata> findTokenMetadataByTokenId(Integer tokenId) {
    return findTokenMetadataByTokenIds(List.of(tokenId)).stream()
        .filter(metadataByTokenEntry -> tokenId.equals(metadataByTokenEntry.getKey()))
        .filter(metadataByTokenEntry -> StringUtils.isEmpty(metadataByTokenEntry.getError()))
        .map(TagEntry::getValue)
        .findFirst();
  }

  public long countPassports() {
    var requestPassportCount = new DataFieldsRequest<>(List.of("all_tokens"));
    return tezosApiGatewayPassportClient
        .getPassportCount(
            digitalPassportContractAddress,
            requestPassportCount
        ).getAllTokens();
  }

  /**
   * Be careful : a multisig can be a multisig for a mint, or a set_pause, or any FA2
   * entry_point....
   */
  public long countMultisigs() {
    var requestMultisigCount = new DataFieldsRequest<>(List.of("multisig_nb"));
    return tezosApiGatewayPassportClient
        .getMultisigCount(
            digitalPassportProxyControllerContractAddress,
            requestMultisigCount
        ).getMultisigNb();
  }

  public Collection<Integer> getTokenIdsByOwner(String ownerAddress) {
    var request = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.TOKENS_BY_OWNER, List.of(ownerAddress))
    ));
    var tokenIdsByOwner = tezosApiGatewayPassportClient.getTokenIdsByOwner(
        digitalPassportContractAddress, request);
    return (tokenIdsByOwner.getTokensByOwner().get(0).getError() == null)
        ? tokenIdsByOwner.getTokensByOwner().get(0).getValue().values()
        : List.of();
  }

  public Map<Integer, String> getOwnersByTokenIds(Collection<Integer> tokenIdList) {
    var tokenOwners = new HashMap<Integer, String>();
    var requestOwner = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.OWNER_BY_TOKEN_ID, tokenIdList)
    ));
    tezosApiGatewayPassportClient.getOwnersByTokenIds(digitalPassportContractAddress, requestOwner)
        .getOwnerBuTokenId()
        .forEach(tagEntry -> tokenOwners.put(
            tagEntry.getKey(),
            tagEntry.getValue())
        );
    return tokenOwners;
  }

  public Map<Integer, String> getOperatorsByTokenIdsAndOwners(Collection<Integer> tokenIdList,
      Map<Integer, String> tokenOwners) {
    var tokenOperators = new HashMap<Integer, String>();
    var requestOperators = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.OPERATORS_BY_TOKEN,
            tokenIdList.stream()
                .map(tokenId -> new TagPair<>(tokenOwners.get(tokenId), tokenId))
                .collect(Collectors.toList())
        )
    ));
    tezosApiGatewayPassportClient.getOperatorsByTokenIdsAndOwners(
            digitalPassportContractAddress, requestOperators)
        .getOperatorsByToken()
        .forEach(tagEntry -> tokenOperators.put(
            tagEntry.getKey().getY(),
            tagEntry.getValue().get(0))
        );
    return tokenOperators;
  }

}
