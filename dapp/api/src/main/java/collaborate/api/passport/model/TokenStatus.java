package collaborate.api.passport.model;

import collaborate.api.passport.model.storage.Multisig;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum TokenStatus {
  @JsonProperty("pending_creation")
  PENDING_CREATION,
  @JsonProperty("created")
  CREATED;

  public static TokenStatus from(Multisig multisig) {
    if (Boolean.TRUE.equals(multisig.getOk())) {
      return CREATED;
    }
    return PENDING_CREATION;
  }
}
