package collaborate.api.businessdata.document.model;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ScopeAssetsDTO {

  private String datasourceId;
  private String scopeName;
  private Collection<ScopeAssetDTO> assets;
}
