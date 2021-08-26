package collaborate.api.user.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.Data;

@Data
public class UserDTO {

    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Instant createdTimestamp;
    private Boolean enabled;
    private Set<RoleDTO> roles;

}
