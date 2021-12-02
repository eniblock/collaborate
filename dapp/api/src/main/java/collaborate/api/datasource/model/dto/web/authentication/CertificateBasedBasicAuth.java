package collaborate.api.datasource.model.dto.web.authentication;

import collaborate.api.datasource.model.dto.web.QueryParam;
import collaborate.api.datasource.model.dto.web.authentication.transfer.PartnerTransferMethod;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.persistence.Transient;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CertificateBasedBasicAuth extends BasicAuth {

  @Transient
  @ToString.Exclude
  @Schema(description = "The passphrase to use for the certificate.<br><i>Local storage only</i>", example = "+FN7m^=GU64$vRdg")
  private String passphrase;

  @Transient
  @JsonIgnore
  @ToString.Exclude
  @Schema(description = "PFX file also known as PKCS #12 , is a single, password protected certificate archive that contains the entire certificate chain plus the matching private key.")
  private byte[] pfxFileContent;

  @Builder(toBuilder = true)
  public CertificateBasedBasicAuth(
      PartnerTransferMethod partnerTransferMethod,
      String user, String password,
      List<QueryParam> queryParams, String passphrase) {
    super(partnerTransferMethod, user, password, queryParams);
    this.passphrase = passphrase;
  }

  @Override
  public <T> T accept(AuthenticationVisitor<T> visitor) {
    return visitor.visitCertificateBasedBasicAuth(this);
  }
}
