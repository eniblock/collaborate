package collaborate.api.datasource.nft;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import collaborate.api.ipfs.IpfsService;
import collaborate.api.tag.model.TagEntry;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

  private final IpfsService ipfsService;
  private final TokenDAO tokenDAO;

  public Optional<Map<String, String>> getOnChainMetadataByTokenId(String contractAddress,
      Integer tokenId) {
    return tokenDAO.getMetadataByTokenId(contractAddress, tokenId).map(TagEntry::asMapOfString);
  }

  public Optional<Map<String, JsonNode>> getOffChainMetadataByTokenId(String contract, Integer tokenId) {
    var onChainMetadataOpt = getOnChainMetadataByTokenId(contract, tokenId);
    Map<String, JsonNode> offChainMetadata = new TreeMap<>();
    onChainMetadataOpt.flatMap(onChainMetadata -> ofNullable(onChainMetadata.get(EMPTY)))
        .map(ipfsService::catJson)
        .ifPresent(jsonNode -> jsonNode.fields().forEachRemaining(
            jsonEntry -> offChainMetadata.put(jsonEntry.getKey(), jsonEntry.getValue()))
        );
    return offChainMetadata.isEmpty() ? Optional.empty() : Optional.of(offChainMetadata);
  }

}
