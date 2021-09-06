package collaborate.api.passport.model.storage;

import java.time.LocalDateTime;
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

  private LocalDateTime createdAt;

  private Integer multisigId;

  private String vin;

  private String datasourceUUID;

}
