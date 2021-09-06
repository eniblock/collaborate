package collaborate.api.tag.model.storage;

import static java.util.Collections.emptyList;

import collaborate.api.tag.model.Key;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * Implementing <code>Entry<String, Collection<Key>></code> is a requirement for serialization
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexerQuery<T> implements Entry<String, Collection<Key<T>>>{

  String key;
  Collection<Key<T>> value;

  public IndexerQuery(@NotNull String indexName, @Nullable T singleValue) {
    this.key = indexName;
    if (singleValue != null) {
      this.value = List.of(new Key<>(singleValue));
    }else{
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