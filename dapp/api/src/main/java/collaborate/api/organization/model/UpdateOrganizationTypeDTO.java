package collaborate.api.organization.model;

import collaborate.api.organization.tag.Organization;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrganizationTypeDTO {
  private Organization update;
  private String remove;

  @JsonIgnore
  public String getAddress(){
    // Should not
    return Optional.ofNullable(update)
        .map(Organization::getAddress)
        .orElse(remove);
  }
}
