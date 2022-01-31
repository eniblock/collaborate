package collaborate.api.datasource.multisig.model;

import collaborate.api.datasource.multisig.model.callparam.CallParam;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionBuildParam {

  private boolean buildAndSign;

  private CallParam callParams;

  private long multisigId;

  private List<String> signers;

}
