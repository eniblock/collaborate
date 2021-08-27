package collaborate.api.user.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.Data;

@Data
public class UserDTO {

  @Schema(description = "The user identifier", example = "23497874-ee3b-406a-9f0f-784f9e8fedd8")
  private UUID id;

  @Schema(description = "The user firstname", example = "John")
  private String firstName;

  @Schema(description = "The user lastname", example = "Doe")
  private String lastName;

  @Schema(description = "The username used for login", example = "john.doe.3765")
  private String username;

  @Schema(description = "The user email address", example = "john.doe.3765@domain.com")
  private String email;

  @Schema(description = "When the user account has been created")
  private Instant createdTimestamp;

  @Schema(description = "Does the user account is enabled ?")
  private Boolean enabled;

  @ArraySchema(schema = @Schema(implementation = RoleDTO.class), uniqueItems = true)
  private Set<RoleDTO> roles;

}
