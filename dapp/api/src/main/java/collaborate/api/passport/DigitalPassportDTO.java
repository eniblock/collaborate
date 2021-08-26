package collaborate.api.passport;

import io.swagger.v3.oas.annotations.media.Schema;
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

  @Schema(description = "The Data Service Provider Public Key Hash", example = "tz1NSuGfg7Tfy8WUxrqWjRSVtTtW8HCMUegV", required = true)
  private String dspAddress;

  @Schema(description = "The Data Service Provider name", example = "PSA", required = true)
  private String dspName;

  @Schema(description = "The vehicle owner email", example = "vehicle@owner.net", required = true)
  private String vehicleOwnerMail;

  @Schema(description = "The datasource Universal Unique Identifier (UUID) where the vehicle data (metrics) are stored. Mostly, this datasource is own by the DSP", example = "ab357d94-04da-4695-815e-24c569fd3a49", required = true)
  private UUID datasourceUUID;

  @Schema(description = "The Vehicle Identification Number (VIN).", example = "5NPET4AC8AH593530", required = true)
  private String vin;

}
