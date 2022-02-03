package collaborate.api.tag.model.storage;

import java.util.Collection;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Implementing a set for TAG. {1,234,53} is the json : { "0": 1, "1": 234, "2": 53 }
 */
public class TagSet<E> extends HashMap<Integer, E> {

  Collection getValues() {
    return this.values();
  }

}
