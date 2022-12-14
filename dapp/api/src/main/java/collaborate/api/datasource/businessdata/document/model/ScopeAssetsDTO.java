package collaborate.api.datasource.businessdata.document.model;

import collaborate.api.datasource.passport.model.AccessStatus;
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
  @Schema(description = "The unique identifier of the datasource", example = "c36f12b9-d98c-4450-8fb8-93960466b45d", required = true)
  private String datasourceId;
  @Schema(description = "The provider wallet address", example = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
  private String providerAddress;
  @Schema(description = "The dataset scope to the relying provider access control system (ex: the OAuth2 scope)", example = "referentials")
  private String scopeName;
  // TODO COL-557 report into the front
  @Schema(description = "The nft token id holding the assets", example = "1")
  private Integer nftId;

  private Collection<BusinessDataDocument> assets;
}
