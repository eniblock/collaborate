package collaborate.api.businessdata.access.grant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccessGrantParams implements Serializable {

  @NotNull
  private UUID accessRequestsUuid;
  @JsonProperty("access_token_hash")
  @NotNull
  private String cipheredToken;

  @NotNull
  private String requesterAddress;
}
