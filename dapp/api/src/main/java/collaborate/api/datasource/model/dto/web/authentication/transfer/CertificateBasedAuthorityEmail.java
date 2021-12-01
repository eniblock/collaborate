package collaborate.api.datasource.model.dto.web.authentication.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateBasedAuthorityEmail implements PartnerTransferMethod {

  private String email;
}
