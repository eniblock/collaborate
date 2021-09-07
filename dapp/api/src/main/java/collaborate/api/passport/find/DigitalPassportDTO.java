package collaborate.api.passport.find;

import collaborate.api.config.ISO8601JsonStringFormat;
import collaborate.api.passport.DigitalPassportStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigitalPassportDTO {

  @Schema(description = "The Data Service Provider Public Key Hash", example = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV")
  private String dspAddress;

  @Schema(description = "The Data Service Provider name", example = "PSA")
  private String dspName;

  @Schema(description = "The datasource Universal Unique Identifier (UUID) where the vehicle data (metrics) are stored. Mostly, this datasource is own by the DSP", example = "ab357d94-04da-4695-815e-24c569fd3a49")
  private UUID datasourceUUID;

  @Schema(description = "The Vehicle Identification Number (VIN).", example = "5NPET4AC8AH593530")
  private String vin;

  @Schema(description = "The Id of the multisig contracts", example = "2")
  private Integer contractId;

  @Schema(description = "The Id of the NFT passport (null if it is not minted)", example = "2")
  private Integer tokenId;

  @Schema(description = "The status of the passport", example = "2")
  private DigitalPassportStatus status;

  @Schema(description = "The passport creation date at ISO-8601 format", example = "2001-07-04T12:08:56.235-07:00")
  @ISO8601JsonStringFormat
  private ZonedDateTime createdAt;

}
