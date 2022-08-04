package collaborate.api.user.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TransferDTO {

  private List<TransferTransactionDTO> transactions;
  private String callerId;
}
