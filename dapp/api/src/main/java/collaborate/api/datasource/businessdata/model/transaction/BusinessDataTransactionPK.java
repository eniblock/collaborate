package collaborate.api.datasource.businessdata.model.transaction;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessDataTransactionPK implements Serializable {

  private String smartContract;

  private String tokenId;

}
