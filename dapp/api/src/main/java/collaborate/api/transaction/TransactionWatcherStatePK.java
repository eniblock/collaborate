package collaborate.api.transaction;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionWatcherStatePK implements Serializable {

  private String smartContract;
  private String property;
}
