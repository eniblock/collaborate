package collaborate.api.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class TransactionWatcherProperty {

  @NotNull
  @Positive
  private Long fixedDelayInMs;
  @NotEmpty
  private String smartContractAddress;

  @JsonIgnore
  public boolean isSmartContract(String address) {
    return smartContractAddress.equals(address);
  }

}
