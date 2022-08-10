package collaborate.api.organization.tag;

import collaborate.api.organization.model.OrganizationDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.ToString;

@Getter
@JsonInclude(Include.NON_NULL)
@ToString
public class UpdateOrganizationType {
  private final Organization update;
  private final String remove;

  UpdateOrganizationType(Organization update, String remove) {
    this.update = update;
    this.remove = remove;
  }
}
