package collaborate.api.tag.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@Data
@NoArgsConstructor
public class TezosIndexer {

  @Schema(description = "The Tezos indexer name")
  private String name;
  @Schema(description = "The Tezos indexer API base URL")
  @JsonProperty("URL")
  private String url;
}
