package collaborate.api.passport.find;

import collaborate.api.config.api.ApiProperties;
import collaborate.api.passport.model.storage.Multisig;
import collaborate.api.passport.model.storage.StorageFields;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FindPassportDAO {

  private final ApiProperties apiProperties;
  private final TezosApiGatewayPassportClient tezosApiGatewayPassportClient;

  public PassportsIndexerTagResponseDTO findPassportsIndexersByDsps(
      Collection<String> dspAddresses) {
    var requestPassportsIndexer = new DataFieldsRequest<>(List.of(
        new MapQuery<String>().init(StorageFields.NFT_INDEXER, dspAddresses)
    ));
    return tezosApiGatewayPassportClient
        .getPassportsIndexer(
            apiProperties.getDigitalPassportContractAddress(),
            requestPassportsIndexer
        );
  }

  public MultisigTagResponseDTO findMultisigByIds(Collection<Integer> multisigIds) {
    var requestMultisigs = new DataFieldsRequest<>(List.of(
        new MapQuery<Integer>().init(StorageFields.MULTISIGS, multisigIds)
    ));
    return tezosApiGatewayPassportClient
        .getMultisigs(
            apiProperties.getDigitalPassportContractAddress(),
            requestMultisigs
        );
  }

  public TokenMetadataResponseDTO findTokenMetadataByTokenIds(Collection<Integer> tokenIds) {
    var requestTokenMetadata = new DataFieldsRequest<>(List.of(
        new MapQuery<Integer>().init(StorageFields.TOKEN_METADATA, tokenIds)
    ));
    return tezosApiGatewayPassportClient
        .getTokenMetadata(
            apiProperties.getDigitalPassportContractAddress(),
            requestTokenMetadata
        );
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
        new MapQuery<String>().init(StorageFields.TOKEN_ID_BY_ASSET_ID, List.of(assetId))
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
