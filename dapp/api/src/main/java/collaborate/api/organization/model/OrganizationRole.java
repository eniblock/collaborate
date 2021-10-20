package collaborate.api.organization.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum OrganizationRole {
  DSP(1),
  BSP(2);

  int code;

  OrganizationRole(int code) {
    this.code = code;
  }

  @JsonValue
  public int getCode() {
    return code;
  }
}
