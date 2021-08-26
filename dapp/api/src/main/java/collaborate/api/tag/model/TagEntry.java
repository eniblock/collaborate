package collaborate.api.tag.model;

import lombok.Data;

@Data
public class TagEntry<K, V> {

  private K key;

  private V value;

}