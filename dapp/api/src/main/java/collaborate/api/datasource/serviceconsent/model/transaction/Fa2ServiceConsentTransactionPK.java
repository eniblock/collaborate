package collaborate.api.datasource.serviceconsent.model.transaction;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fa2ServiceConsentTransactionPK implements Serializable {

  private String smartContract;
  private Long tokenId;
}
