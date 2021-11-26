package collaborate.api.datasource.passport.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AccessStatus {
  @JsonProperty("pending")
  PENDING,
  @JsonProperty("granted")
  GRANTED,
  @JsonProperty("locked")
  LOCKED
}
