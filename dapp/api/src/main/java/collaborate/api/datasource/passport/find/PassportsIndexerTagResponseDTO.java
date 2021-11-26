package collaborate.api.datasource.passport.find;

import collaborate.api.datasource.passport.model.storage.PassportsIndexer;
import collaborate.api.tag.model.TagEntry;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassportsIndexerTagResponseDTO {

  // Keys are dsp walletAddress
  @JsonProperty("nft_indexer")
  private List<TagEntry<String, PassportsIndexer>> passportsIndexerByDsp = new ArrayList<>();

}
