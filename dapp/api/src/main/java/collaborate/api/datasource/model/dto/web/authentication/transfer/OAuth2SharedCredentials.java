package collaborate.api.datasource.model.dto.web.authentication.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class OAuth2SharedCredentials extends PartnerTransferMethod {

  public static final String TYPE_NAME = "OAuth2SharedCredentials";

  @Override
  public <T> T accept(TransferMethodVisitor<T> visitor) {
    return visitor.visitOAuth2SharedCredentials(this);
  }
}
