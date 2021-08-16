package collaborate.api.datasource.domain.authentication;

import collaborate.api.datasource.domain.web.QueryParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Transient;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class CertificateBasedBasicAuth extends BasicAuth {

  private String host;
  @Transient
  @ToString.Exclude
  private String passphrase;
  private String caEmail;
  @Transient
  @JsonIgnore
  private byte[] pfxFileContent;

  @Builder(toBuilder = true)
  public CertificateBasedBasicAuth(String user, String password,
      List<QueryParam> queryParams, String host, String passphrase,
      String caEmail) {
    super(user, password, queryParams);
    this.host = host;
    this.passphrase = passphrase;
    this.caEmail = caEmail;
  }
}
