package collaborate.api.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class TransactionWatcherProperty {

  private Long fixedDelayInMs;
  private String smartContractAddress;

  @JsonIgnore
  public boolean isSmartContract(String address) {
    return smartContractAddress.equals(address);
  }

}
