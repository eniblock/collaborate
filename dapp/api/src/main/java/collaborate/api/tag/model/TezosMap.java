package collaborate.api.tag.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TezosMap<K, V> {

  private int size;

  private TagEntry<K, V>[] value;

  public Optional<V> findValue(K key) {
    return Arrays.stream(value)
        .filter(tagEntry -> tagEntry.getKey().equals(key))
        .map(TagEntry::getValue)
        .findFirst();
  }

  public Collection<Object> values() {
    if (value != null) {
      return Arrays.stream(value).map(TagEntry::getValue)
          .collect(Collectors.toCollection(ArrayList::new));
    } else {
      return Collections.emptyList();
    }
  }
}
