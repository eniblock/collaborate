package collaborate.api.datasource.model.dto.web.authentication.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
public class OAuth2ClientCredentials extends PartnerTransferMethod {

  public static final String TYPE_NAME = "OAuth2ClientCredentials";

  @Override
  public <T> T accept(TransferMethodVisitor<T> visitor) {
    return visitor.visitOAuth2ClientCredentials(this);
  }
}
