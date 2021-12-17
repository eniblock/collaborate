package collaborate.api.tag.model.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Implementing a pair for TAG.
 * (x,y) is the json : {
 *                         "0": x,
 *                         "1": y
 *                     }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagPair<E, F> {

  @JsonProperty("0")
  E x;

  @JsonProperty("1")
  F y;
}
