package collaborate.api.businessdata.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CatalogScopeDetails {

  private String organizationId;
  private String organizationName;
  private String datasourceId;
  private String scope;
  private String assetId;
  private String status;
}
