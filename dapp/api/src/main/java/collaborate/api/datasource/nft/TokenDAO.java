package collaborate.api.datasource.nft;

import static org.apache.commons.lang3.StringUtils.isEmpty;

import collaborate.api.datasource.nft.model.storage.TokenMetadata;
import collaborate.api.datasource.passport.model.storage.StorageFields;
import collaborate.api.tag.model.Bytes;
import collaborate.api.tag.model.TagEntry;
import collaborate.api.tag.model.storage.DataFieldsRequest;
import collaborate.api.tag.model.storage.MapQuery;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenDAO {

  private final TagTokenMetadataClient tagClient;

  public Optional<List<TagEntry<String, Bytes>>> findMetadataByTokenId(String contract, Integer tokenId) {
    var requestTokenMetadata = new DataFieldsRequest<>(List.of(
        new MapQuery<>(StorageFields.TOKEN_METADATA, List.of(tokenId))
    ));
    var metadataResponse = tagClient.getTokenMetadata(contract, requestTokenMetadata);
    return metadataResponse.getTokenMetadata()
        .stream()
        .filter(entry -> tokenId.equals(entry.getKey()))
        .filter(entry -> isEmpty(entry.getError()))
        .map(TagEntry::getValue)
        .map(TokenMetadata::getTokenInfo)
        .findFirst();
  }
}
