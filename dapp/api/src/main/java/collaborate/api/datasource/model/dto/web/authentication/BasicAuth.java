package collaborate.api.datasource.model.dto.web.authentication;

import collaborate.api.datasource.model.dto.web.QueryParam;
import collaborate.api.datasource.model.dto.web.authentication.transfer.PartnerTransferMethod;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BasicAuth extends Authentication {

  @Schema(description = "The user credential part", example = "john")
  @Transient
  protected String user;

  @ToString.Exclude
  @Transient
  @Schema(description = "The password credential part", example = "mgZbBcmx*Ydh3^3a")
  protected String password;

  @ArraySchema(schema = @Schema(description = "query parameter to add to the URL for each datasource resource call"))
  @OneToMany(cascade = CascadeType.ALL)
  protected List<QueryParam> queryParams;

  public BasicAuth(PartnerTransferMethod partnerTransferMethod, String user, String password,
      List<QueryParam> queryParams) {
    super(null, partnerTransferMethod);
    this.user = user;
    this.password = password;
    this.queryParams = queryParams;
  }

  @Override
  public <T> T accept(AuthenticationVisitor<T> visitor) {
    return visitor.visitBasicAuth(this);
  }
}
