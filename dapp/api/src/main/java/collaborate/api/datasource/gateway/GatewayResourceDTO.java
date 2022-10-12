package collaborate.api.datasource.gateway;

import collaborate.api.datasource.model.Metadata;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayResourceDTO {

  private String datasourceId;
  private String alias;
  private String assetIdForDatasource;
  private Set<Metadata> metadata = new HashSet<>();
}
