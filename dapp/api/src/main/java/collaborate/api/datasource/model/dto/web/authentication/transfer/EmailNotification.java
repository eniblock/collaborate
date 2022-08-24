package collaborate.api.datasource.model.dto.web.authentication.transfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EmailNotification extends PartnerTransferMethod {

  public static final String TYPE_NAME = "EmailNotification";
  private String email;

  @Override
  public <T> T accept(TransferMethodVisitor<T> visitor) {
    return visitor.visitEmailNotification(this);
  }
}
