package collaborate.api.tag.model;

import static java.util.stream.Collectors.toMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TagEntry<K, V> {

  private K key;
  private V value;
  private String error;

  /**
   * @return The tag entries as a map,<br> <b>Warning</b> an entry can be associated to a null value
   * when the {@link TagEntry} has error
   */
  public static <K, V> Map<K, V> asMap(Collection<TagEntry<K, V>> tagEntries) {
    return tagEntries.stream()
        .collect(toMap(
            TagEntry::getKey,
            TagEntry::getValue,
            (a, b) -> {
              throw new IllegalStateException("Duplicate key");
            },
            TreeMap::new));
  }

  public static <K, V> Map<String, String> asMapOfString(Collection<TagEntry<K, V>> tagEntries) {
    return tagEntries.stream()
        .collect(toMap(
            e -> e.getKey().toString(),
            e -> e.getValue().toString(),
            (a, b) -> {
              throw new IllegalStateException("Duplicate key");
            },
            TreeMap::new
        ));
  }

  public static <K, V> Optional<V> findFirstNonNullValueByKey(Collection<TagEntry<K, V>> tagEntries,
      K key) {
    return tagEntries.stream().filter(e -> e.getKey().equals(key))
        .filter(e -> Objects.nonNull(e.getValue()))
        .map(TagEntry::getValue)
        .findFirst();
  }
}