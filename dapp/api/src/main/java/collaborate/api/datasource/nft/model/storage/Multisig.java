package collaborate.api.datasource.nft.model.storage;

import collaborate.api.tag.model.Bytes;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Collection;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Multisig {

  private String actionToPerform;
  private String addr1;
  private String addr2;
  private String param1;
  private Bytes param2;
  private Collection<MultisigParticipant> participants;
  @NotNull
  private Boolean ok;
  private Integer threshold;
  private Integer weight;

}
