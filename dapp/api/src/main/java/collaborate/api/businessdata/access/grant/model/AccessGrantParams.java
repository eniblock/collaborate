package collaborate.api.businessdata.access.grant.model;

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
public class AccessGrantParams implements Serializable {

  @NotNull
  private UUID id;

  @NotNull
  private String requesterAddress;

  @NotNull
  private String providerAddress;

  @NotNull
  private String jwtToken;

}
