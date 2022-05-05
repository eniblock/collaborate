package collaborate.api.datasource.nft.catalog.create;

import collaborate.api.datasource.nft.model.metadata.TZip21Metadata;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetDTO {

  @Schema(description = "The datasource Universal Unique Identifier (UUID) where the vehicle data (metrics) are stored. Mostly, this datasource is own by the DSP", example = "ab357d94-04da-4695-815e-24c569fd3a49", required = true)
  @NotNull
  private UUID datasourceUUID;

  @NotNull
  private String assetRelativePath;

  @Schema(description = "The Asset Identifier used by the datasource provider api.", example = "124091f9115613c46574764", required = true)
  private String assetIdForDatasource;

  private String assetType;

  private TZip21Metadata tZip21Metadata;
}
