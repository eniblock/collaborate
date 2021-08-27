package collaborate.api.passport.create;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePassportDTO {

  @Schema(description = "The Vehicle Owner (VO) public-key-hash.", example = "tz1hk3ncAVaNR8eBYkcgwdRLhaCYvqsnrbtz", required = true)
  @NotNull
  // TODO add an email validator
  private String vehicleOwnerMail;

  @Schema(description = "The datasource Universal Unique Identifier (UUID) where the vehicle data (metrics) are stored. Mostly, this datasource is own by the DSP", example = "ab357d94-04da-4695-815e-24c569fd3a49", required = true)
  @NotNull
  private UUID datasourceUUID;

  @Schema(description = "The Vehicle Identification Number (VIN).", example = "5NPET4AC8AH593530", required = true)
  @NotNull
  private String vin;
}
