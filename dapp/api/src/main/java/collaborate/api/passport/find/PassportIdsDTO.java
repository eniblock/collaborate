package collaborate.api.passport.find;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PassportIdsDTO {

  /**
   * Ids of "multisigs" of passports that wait VO consent
   */
  private Collection<Integer> passportsWaitingConsent;

  /**
   * Ids of "tokens" that are passport
   */
  private Collection<Integer> passportsConsented;

  public PassportIdsDTO() {
    this.passportsWaitingConsent = Collections.emptyList();
    this.passportsConsented = Collections.emptyList();
  }

  public PassportIdsDTO(Map<String, Integer> passportsWithoutConsent,
      Map<String, Integer> passportsConsented) {
    this(
        passportsWithoutConsent == null ? new ArrayList<>() : passportsWithoutConsent.values(),
        passportsConsented == null ? new ArrayList<>() : passportsConsented.values()
    );
  }
}
