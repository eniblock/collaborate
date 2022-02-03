package collaborate.api.datasource.multisig.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProxyTokenControllerTransactionPK implements Serializable {

  private String smartContract;
  private Long multiSigId;
}
