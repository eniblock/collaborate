package collaborate.api.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CleanUserService {

  public String cleanUserId(String userID) {
    String cleaned = userID.replace("@", "_._xdev-at_._");
    cleaned = cleaned.replace(":", "_._xdev-sem_._");
    log.debug("userId {{}} cleaned as {{}}", userID, cleaned);
    return cleaned;
  }

  public String uncleanUserId(String userID) {
    String uncleaned = userID.replace("_._xdev-at_._", "@");
    uncleaned = uncleaned.replace("_._xdev-sem_._", ":");
    log.debug("userId {{}} uncleaned as {{}}", userID, uncleaned);
    return uncleaned;
  }
}
