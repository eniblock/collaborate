package collaborate.api.passport.model.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PassportsIndexerToken {

  @JsonProperty("token_key_ref")
  private Integer tokenId;

  private String tokenOwnerAddress;

  private String assetId;
}