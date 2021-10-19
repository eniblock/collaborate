package collaborate.api.datasource.model.dto.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum DatasourceStatus {
  @JsonProperty("1")
  CREATED,

  @JsonProperty("2")
  SYNCHRONIZED,
}
