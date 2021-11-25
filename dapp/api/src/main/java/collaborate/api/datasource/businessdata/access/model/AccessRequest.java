package collaborate.api.datasource.businessdata.access.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccessRequest {

  private UUID id;
  @JsonProperty("nft_id")
  private Integer tokenId;
  @JsonProperty("access_token_hash")
  private String jwtToken;
  private String providerAddress;
  private String requesterAddress;
  private Boolean accessGranted;
  private List<String> scopes;
}
