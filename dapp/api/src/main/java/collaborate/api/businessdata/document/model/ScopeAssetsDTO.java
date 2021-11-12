package collaborate.api.businessdata.document.model;

import java.util.Collection;
import lombok.Data;

@Data
public class ScopeAssetsDTO {

  private String datasourceId;
  private String scopeName;
  private Collection<ScopeAssetDTO> assets;
}
