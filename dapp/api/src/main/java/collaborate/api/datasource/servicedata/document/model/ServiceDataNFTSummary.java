package collaborate.api.datasource.servicedata.document.model;

import collaborate.api.config.ISO8601JsonStringFormat;
import collaborate.api.datasource.passport.model.AccessStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDataNFTSummary {

  @Schema(description = "The status of the the asset", example = "GRANTED")
  private AccessStatus accessStatus;
  @Schema(description = "The unique identifier of the datasource", example = "c36f12b9-d98c-4450-8fb8-93960466b45d", required = true)
  private String datasourceId;
  @Schema(description = "The provider wallet address", example = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
  private String providerAddress;
  @Schema(description = "The dataset scope to the relying provider access control system (ex: the OAuth2 scope)", example = "referentials")
  private String scopeName;
  @ISO8601JsonStringFormat
  @Schema(description = "The creation date of the NFT", example = "2019-03-27T10:15:30")
  private ZonedDateTime createdDatetime;
  @Schema(description = "A link to visualize the NFT in a blockchain explorer", example = "https://jakartanet.tzkt.io/KT1UJQVShZb2Dwb2Y5nWR37xceXaZJSA4Sjy/storage/21737")
  private String blockchainExplorerPreview;
}
