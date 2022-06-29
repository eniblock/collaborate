package collaborate.api.tag.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class TagConfig {

  @Schema(description = "The Tezos RPC node URL used for sending blockchain transactions", example = "https://jakartanet.smartpy.io")
  private Set<String> tezosNodesURLs;
  @Schema(description = "The Tezos indexer used for reading blockchain")
  private Set<TezosIndexer> tezosIndexers;

  @JsonIgnore
  public Optional<String> findIndexerUrlByName(String name) {
    return tezosIndexers.stream()
        .filter(i -> name.equals(i.getName()))
        .map(TezosIndexer::getUrl)
        .findFirst();
  }

}
