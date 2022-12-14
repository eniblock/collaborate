package collaborate.api.datasource.serviceconsent.consent;

import collaborate.api.tag.model.user.UserWalletDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentServiceConsentDTO {

  private Integer contractId;
  private UserWalletDTO vehicleOwnerUserWallet;
}
