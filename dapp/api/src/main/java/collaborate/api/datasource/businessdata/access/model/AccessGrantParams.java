package collaborate.api.datasource.businessdata.access.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccessGrantParams implements Serializable {

  @JsonProperty("access_token_hash")
  @NotNull
  private String cipheredToken;

  @NotNull
  private String requesterAddress;

  @NotNull
  private Integer nftId;
}
