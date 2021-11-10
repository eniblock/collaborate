package collaborate.api.config;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UUIDGenerator {

  public UUID randomUUID() {
    return UUID.randomUUID();
  }

}
