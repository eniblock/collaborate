package collaborate.api.datasource.passport.model;

import collaborate.api.datasource.nft.model.storage.Multisig;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum TokenStatus {
  @JsonProperty("pending_creation")
  PENDING_CREATION,
  @JsonProperty("created")
  CREATED;

}
