package collaborate.api.passport.model.storage;

import collaborate.api.config.ISO8601JsonStringFormat;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassportMetadata {

  private String dspAddress;

  @ISO8601JsonStringFormat
  private ZonedDateTime createdAt;

  private Integer multisigId;

  private String vin;

  private String datasourceUUID;

}
