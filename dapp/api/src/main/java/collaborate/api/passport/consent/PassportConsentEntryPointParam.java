package collaborate.api.passport.consent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassportConsentEntryPointParam {

  private Integer contractId;
  private String vehicleOwnerAddress;
}
