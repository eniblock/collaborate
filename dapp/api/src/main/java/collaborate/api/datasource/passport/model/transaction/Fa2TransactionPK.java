package collaborate.api.datasource.passport.model.transaction;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fa2TransactionPK implements Serializable {

  private String smartContract;
  private Long tokenId;
}
