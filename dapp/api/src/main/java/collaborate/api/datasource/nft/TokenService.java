package collaborate.api.datasource.nft;

import static java.util.stream.Collectors.toMap;

import collaborate.api.tag.model.TagEntry;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final TokenDAO tokenDAO;

  public Optional<Map<String, String>> getMetadataByTokenId(String contractAddress,
      Integer tokenId) {
    return tokenDAO.getMetadataByTokenId(contractAddress, tokenId)
        .map(tagEntries ->
            tagEntries.stream().collect(toMap(TagEntry::getKey, t -> t.getValue().toString()))
        );
  }

}
