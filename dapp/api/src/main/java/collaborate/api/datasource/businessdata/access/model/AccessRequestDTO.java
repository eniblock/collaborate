package collaborate.api.datasource.businessdata.access.model;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessRequestDTO {

  @Schema(description = "The NFT identifier", example = "1", required = true)
  @NotNull
  private Integer tokenId;

  // FIXME COL-569
  @Deprecated
  @Schema(description = "The unique identifier of the datasource", example = "c36f12b9-d98c-4450-8fb8-93960466b45d", required = true)
  @NotBlank
  private String datasourceId;

  // FIXME COL-569
  @Deprecated
  @Schema(description = "The Asset identifier used by the datasource provider api.", example = "124091f9115613c46574764", required = true)
  @NotBlank
  private String assetIdForDatasource;

  @Schema(description = "The provider wallet address", example = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
  @NotBlank
  private String providerAddress;
}
