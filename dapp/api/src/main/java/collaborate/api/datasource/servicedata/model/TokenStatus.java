package collaborate.api.datasource.servicedata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TokenStatus {
  @JsonProperty("pending_creation")
  PENDING_CREATION,
  @JsonProperty("created")
  CREATED;

}
