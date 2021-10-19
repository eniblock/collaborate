package collaborate.api.passport.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AccessStatus {
  @JsonProperty("granted")
  GRANTED,
  @JsonProperty("no_access")
  NO_ACCESS,
  @JsonProperty("pending")
  PENDING
}
