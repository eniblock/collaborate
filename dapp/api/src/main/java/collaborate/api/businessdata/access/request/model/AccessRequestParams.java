package collaborate.api.businessdata.access.request.model;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccessRequestParams implements Serializable {

  private UUID id;
  private String datasourceId;
  private String scope;
  private String requesterAddress;
  private String providerAddress;

}
