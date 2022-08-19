package collaborate.api.datasource.model.dto.web.authentication.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateBasedAuthorityEmail extends PartnerTransferMethod {

  public static final String TYPE_NAME = "CertificateBasedAuthorityEmail";
  private String email;

  @Override
  public <T> T accept(TransferMethodVisitor<T> visitor) {
    return visitor.visitCertificateBasedBasicAuth(this);
  }
}
