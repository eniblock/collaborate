package collaborate.api.datasource.businessdata.find;

import collaborate.api.datasource.nft.model.storage.TokenIndex;
import collaborate.api.tag.model.TagEntry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndexerTagResponseDTO {

  @JsonProperty("nft_indexer")
  private List<TagEntry<String, Map<String, TokenIndex>>> passportsIndexerByDsp = new ArrayList<>();

  @JsonIgnore
  public Stream<TokenIndex> streamTokenIndexes() {
    return passportsIndexerByDsp.stream()
        .map(TagEntry::getValue)
        .filter(Objects::nonNull)
        .map(Map::values)
        .flatMap(Collection::stream);
  }
}
