package collaborate.api.organization.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum OrganizationStatus {
  /**
   * Organization has been initialized but is not member of the consortium in the smart-contract
   */
  INACTIVE(0),
  /**
   * blockchain transaction has been sent to add the organization
   */
  PENDING(1),
  /**
   * The organization is member of the consortium in the smart-contract
   */
  ACTIVE(2);

  final int code;

  OrganizationStatus(int code) {
    this.code = code;
  }

  @JsonValue
  public int getCode() {
    return code;
  }

  @JsonCreator
  public static OrganizationStatus forValues(int code) {
    for (OrganizationStatus role : OrganizationStatus.values()) {
      if (role.code == code) {
        return role;
      }
    }
    return null;
  }
}
