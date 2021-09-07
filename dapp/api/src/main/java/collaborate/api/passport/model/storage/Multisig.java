package collaborate.api.passport.model.storage;

import java.time.ZonedDateTime;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Multisig {

  private String actionToPerform;
  private String addr1;
  private String addr2;
  private String param1;
  private String param2;
  private Collection<MultisigParticipant> participants;
  private Boolean ok;
  private Integer threshold;
  private Integer weight;

  public ZonedDateTime getCreatedAt() {
    return getParticipants().stream()
        .filter(p -> p.getAddress().equals(getAddr1()))
        .findFirst()
        .map(MultisigParticipant::getTimestamp)
        .orElseThrow();
  }
}
