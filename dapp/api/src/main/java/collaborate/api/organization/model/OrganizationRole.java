package collaborate.api.organization.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum OrganizationRole {
  DSP(1),
  BSP(2);

  final int code;

  OrganizationRole(int code) {
    this.code = code;
  }

  @JsonValue
  public int getCode() {
    return code;
  }

  @JsonCreator
  public static OrganizationRole forValues(int code) {
    for (OrganizationRole role : OrganizationRole.values()) {
      if (role.code == code) {
        return role;
      }
    }
    return null;
  }
}
