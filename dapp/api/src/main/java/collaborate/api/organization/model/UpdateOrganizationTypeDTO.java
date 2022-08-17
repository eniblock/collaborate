package collaborate.api.organization.model;

import collaborate.api.organization.tag.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrganizationTypeDTO {

  private Organization update;
  private String remove;

  @JsonIgnore
  public String getAddress() {
    return Optional.ofNullable(update)
        .map(Organization::getAddress)
        .orElse(remove);
  }

  @JsonIgnore
  public boolean isUpdateType() {
    return update != null && StringUtils.isBlank(remove);
  }

  @JsonIgnore
  public boolean isRemoveType() {
    return update == null && StringUtils.isNotBlank(remove);
  }

}
