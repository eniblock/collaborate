package collaborate.api.businessdata.document.model;

import collaborate.api.passport.model.AccessStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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

  @Schema(description = "The status of the the asset", example = "GRANTED")
  private AccessStatus accessStatus;
  private String datasourceId;
  private String scopeName;
  private Collection<ScopeAssetDTO> assets;
}
