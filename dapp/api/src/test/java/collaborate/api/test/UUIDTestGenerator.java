package collaborate.api.test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDTestGenerator {

  private List<UUID> uuids = new ArrayList<>();

  public UUID next() {
    var uuid = UUID.randomUUID();
    uuids.add(uuid);
    return uuid;
  }

  public UUID get(int idx) {
    return uuids.get(idx);
  }


}
