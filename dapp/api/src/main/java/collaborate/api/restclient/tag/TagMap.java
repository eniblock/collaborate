package collaborate.api.restclient.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class TagMap<K, V> {

  private int size;

  private TagEntry<K, V>[] value;

  public Collection<Object> values() {
    if (value != null) {
      return Arrays.stream(value).map(TagEntry::getValue)
          .collect(Collectors.toCollection(ArrayList::new));
    } else {
      return Collections.emptyList();
    }
  }
}