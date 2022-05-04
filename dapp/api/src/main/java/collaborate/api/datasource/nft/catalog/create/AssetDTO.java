package collaborate.api.datasource.nft.catalog.create;

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

  @Schema(description = "A displayable name", example = "My awesome asset", required = true)
  @NotNull
  private String displayName;

  @Schema(description = "The datasource Universal Unique Identifier (UUID) where the vehicle data (metrics) are stored. Mostly, this datasource is own by the DSP", example = "ab357d94-04da-4695-815e-24c569fd3a49", required = true)
  @NotNull
  private UUID datasourceUUID;

  @Schema(description = "The Asset Identifier.", example = "5NPET4AC8AH593530", required = true)
  @NotNull
  private String assetId;

  @Schema(description = "The Asset Identifier used by the datasource provider api.", example = "124091f9115613c46574764", required = true)
  private String assetIdForDatasource;

  private String assetType;
}
