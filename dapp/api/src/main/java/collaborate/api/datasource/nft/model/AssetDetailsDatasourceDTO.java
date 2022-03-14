package collaborate.api.datasource.nft.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetDetailsDatasourceDTO {

  @Schema(description = "The unique identifier of the datasource", example = "c36f12b9-d98c-4450-8fb8-93960466b45d", required = true)
  @NotNull
  private String id;

  @Schema(description = "The Asset identifier used by the datasource provider api.", example = "referentials", required = true)
  @NotNull
  private String assetIdForDatasource;

  private String baseUri;

  @Schema(description = "The datasource owner wallet address", example = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
  private String ownerAddress;


  private Set<String> scopes;

}
