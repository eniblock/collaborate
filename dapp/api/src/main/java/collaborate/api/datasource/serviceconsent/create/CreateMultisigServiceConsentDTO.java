package collaborate.api.datasource.serviceconsent.create;

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
public class CreateMultisigServiceConsentDTO {

  @NotNull
  // TODO add an email validator
  private String assetOwnerMail;

  //@Schema(description = "The datasource Universal Unique Identifier (UUID) where the vehicle data (metrics) are stored. Mostly, this datasource is own by the DSP", example = "ab357d94-04da-4695-815e-24c569fd3a49", required = true)
  //@NotNull
  //private UUID datasourceUUID;
  @NotNull
  private Integer passportId;

  @Schema(description = "The Asset Identifier.", example = "5NPET4AC8AH593530", required = true)
  @NotNull
  private UUID assetId;

  @Schema(description = "The Asset Identifier used by the datasource provider api.", example = "124091f9115613c46574764", required = true)
  private String assetIdForDatasource;
}
