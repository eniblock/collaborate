package collaborate.api.datasource.nft;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import collaborate.api.datasource.nft.model.storage.TokenMetadata;
import collaborate.api.datasource.passport.model.storage.StorageFields;
import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TokenDAO {

  private final TagTokenMetadataClient tagTokenMetadataClient;

  public List<TagEntry<Integer, TokenMetadata>> findByIds(Collection<Integer> tokenIds,
      String smartContract) {
    var requestTokenMetadata = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.TOKEN_METADATA, tokenIds)
    ));
    return tagTokenMetadataClient
        .getTokenMetadata(
            smartContract,
            requestTokenMetadata
        ).getTokenMetadata();
  }

  public Optional<TokenMetadata> findById(Integer tokenId, String smartContract) {
    return findByIds(List.of(tokenId), smartContract).stream()
        .filter(e -> tokenId.equals(e.getKey()))
        .filter(e -> Objects.nonNull(e.getValue()))
        .map(TagEntry::getValue)
        .findFirst();
  }

  public Optional<List<TagEntry<String, Bytes>>> findMetadataByTokenId(String contract, Integer tokenId) {
    var requestTokenMetadata = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.TOKEN_METADATA, List.of(tokenId))
    ));
    var metadataResponse = tagTokenMetadataClient.getTokenMetadata(contract, requestTokenMetadata);
    return metadataResponse.getTokenMetadata()
        .stream()
        .filter(entry -> tokenId.equals(entry.getKey()))
        .filter(entry -> isEmpty(entry.getError()))
        .map(TagEntry::getValue)
        .map(TokenMetadata::getTokenInfo)
        .findFirst();
  }
}
