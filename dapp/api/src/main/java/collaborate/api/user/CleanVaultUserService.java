package collaborate.api.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CleanVaultUserService {

  public String cleanUserId(String userID) {
    String cleaned = userID.replace("@", "_._xdev-at_._");
    cleaned = cleaned.replace(":", "_._xdev-sem_._");
    return cleaned;
  }

  public String uncleanUserId(String userID) {
    String uncleaned = userID.replace("_._xdev-at_._", "@");
    uncleaned = uncleaned.replace("_._xdev-sem_._", ":");
    return uncleaned;
  }
}
