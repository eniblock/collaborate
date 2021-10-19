package collaborate.api.user.model;


import collaborate.api.user.security.Authorizations.Roles;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolesDTO {

  @ArraySchema(schema = @Schema(example = Roles.ASSET_OWNER), uniqueItems = true)
  @NotNull
  private Set<String> rolesNames;
}
