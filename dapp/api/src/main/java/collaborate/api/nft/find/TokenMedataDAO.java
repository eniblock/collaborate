package collaborate.api.nft.find;

import collaborate.api.nft.model.storage.TokenMetadata;
import collaborate.api.passport.model.storage.StorageFields;
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
public class TokenMedataDAO {

  private final TAGTokenMetadataClient tagTokenMetadataClient;

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
}
