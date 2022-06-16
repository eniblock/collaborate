package collaborate.api.tag.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class TagConfig {

  private Set<String> tezosNodesURLs;
  private Set<TezosIndexer> tezosIndexers;

  @JsonIgnore
  public Optional<TezosIndexer> findIndexerUrlByName(String name) {
    return tezosIndexers.stream()
        .filter(i -> name.equals(i.getName()))
        .findFirst();
  }

}
