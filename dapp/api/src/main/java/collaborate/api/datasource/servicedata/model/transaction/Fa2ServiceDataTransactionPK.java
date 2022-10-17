package collaborate.api.datasource.servicedata.model.transaction;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fa2ServiceDataTransactionPK implements Serializable {

  private String smartContract;
  private Long tokenId;
}
