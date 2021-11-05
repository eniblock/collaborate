package collaborate.api.businessdata.access.grant.model;

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
public class AccessGrantParams implements Serializable {

  @NotNull
  private String scopeId;

  @NotNull
  private String requesterAddress;

  @NotNull
  private String providerAddress;

  @NotNull
  private String jwtToken;

}
