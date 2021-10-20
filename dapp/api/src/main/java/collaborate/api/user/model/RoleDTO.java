package collaborate.api.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {

  @Schema(description = "The role identifier")
  private UUID id;

  @Schema(description = "The role name")
  private String name;

  @Schema(description = "The role description")
  private String description;

}
