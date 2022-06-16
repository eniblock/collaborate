package collaborate.api.tag.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class TezosIndexer {

  private String name;
  @JsonProperty("URL")
  private String url;
}
