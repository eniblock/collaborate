package collaborate.api.organization.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum OrganizationRole {
  @JsonProperty("1")
  DSP,

  @JsonProperty("2")
  BSP
}
