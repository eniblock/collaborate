package collaborate.api.tag.model.storage;

import static java.util.Collections.emptyList;

import collaborate.api.tag.model.Key;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Implementing <code>Entry<String, Collection<Key>></code> is a requirement for serialization
 */
@Data
@NoArgsConstructor
public class MapQuery<T> implements Entry<String, Collection<Key<T>>> {

  String key;
  Collection<Key<T>> value;

  public MapQuery(@NotNull String indexName, Collection<T> multipleValues) {
    this.key = indexName;
    if (multipleValues != null) {
      this.value = multipleValues.stream()
          .map(Key::new)
          .collect(Collectors.toList());
    } else {
      this.value = emptyList();
    }
  }

  @Override
  public Collection<Key<T>> setValue(Collection<Key<T>> value) {
    var previousValue = this.value;
    this.value = value;
    return previousValue;
  }
}
