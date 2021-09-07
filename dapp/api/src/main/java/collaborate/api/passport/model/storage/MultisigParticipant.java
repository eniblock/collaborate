package collaborate.api.passport.model.storage;

import collaborate.api.config.ISO8601JsonStringFormat;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultisigParticipant {

  private String address;
  private Boolean hasVoted;

  @ISO8601JsonStringFormat
  private ZonedDateTime timestamp;
}
