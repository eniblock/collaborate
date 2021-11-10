package collaborate.api.passport.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetDataCatalogDTO {

  @Schema(description = "The list of datasources associated to a passport (null if it is not minted)")
  private List<DatasourceDTO> datasources;

}
