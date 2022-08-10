package collaborate.api.organization.model;

import collaborate.api.organization.tag.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@JsonInclude(Include.NON_NULL)
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
  public boolean isUpdate() {
    return update != null && StringUtils.isBlank(remove);
  }

  @JsonIgnore
  public boolean isRemove() {
    return update == null && StringUtils.isNotBlank(remove);
  }

}
