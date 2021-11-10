package collaborate.api.passport.find;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.nft.model.storage.Multisig;
import collaborate.api.nft.model.storage.TokenMetadata;
import collaborate.api.passport.model.storage.StorageFields;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FindPassportDAO {

  private final ApiProperties apiProperties;
  private final TezosApiGatewayPassportClient tezosApiGatewayPassportClient;

  public PassportsIndexerTagResponseDTO findPassportsIndexersByDsps(
      Collection<String> dspAddresses) {
    var requestPassportsIndexer = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.NFT_INDEXER, dspAddresses)
    ));
    return tezosApiGatewayPassportClient
        .getPassportsIndexer(
            apiProperties.getDigitalPassportContractAddress(),
            requestPassportsIndexer
        );
  }

  public MultisigTagResponseDTO findMultisigByIds(Collection<Integer> multisigIds) {
    var requestMultisigs = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.MULTISIGS, multisigIds)
    ));
    return tezosApiGatewayPassportClient
        .getMultisigs(
            apiProperties.getDigitalPassportContractAddress(),
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
            apiProperties.getDigitalPassportContractAddress(),
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

  public long count() {
    var requestPassportCount = new DataFieldsRequest<>(List.of("all_tokens"));
    return tezosApiGatewayPassportClient
        .getPassportCount(
            apiProperties.getDigitalPassportContractAddress(),
            requestPassportCount
        ).getAllTokens();
  }

  public Optional<Integer> findTokenIdByAssetId(String assetId) {
    var requestTokenMetadata = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.TOKEN_ID_BY_ASSET_ID, List.of(assetId))
    ));
    return tezosApiGatewayPassportClient
        .getTokenIdByAssetIds(
            apiProperties.getDigitalPassportContractAddress(),
            requestTokenMetadata
        ).getTokenIdByAssetId().stream()
        .findFirst()
        .map(TagEntry::getValue);
  }

  public Optional<Multisig> findMultisigById(Integer contractId) {
    return findMultisigByIds(List.of(contractId))
        .getMultisigs().stream()
        .findFirst()
        .map(TagEntry::getValue);
  }
}
