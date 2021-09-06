package collaborate.api.tag.model.storage;

import collaborate.api.tag.model.TagEntry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IndexerQueryResponse<T> extends
    HashMap<String, Collection<TagEntry<String, Map<String, T>>>> {

  public Map<String, T> getFirstEntryValue(String key) {
    return Optional.ofNullable(get(key))
        .flatMap(entries -> entries.stream().findFirst())
        .flatMap(entry -> Optional.ofNullable(entry.getValue()))
        .orElse(Collections.emptyMap());
  }

}
