package collaborate.api.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferTransactionDTO {

  private String to;
  /**
   * 1 XTZ = 10^6 mutez
   */
  @JsonProperty("amount")
  private int amountInMutez;
}
