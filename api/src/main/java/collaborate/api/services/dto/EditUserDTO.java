package collaborate.api.services.dto;


import javax.validation.constraints.NotNull;
import java.util.Set;

public class EditUserDTO {
    @NotNull
    private Set<String> rolesNames;

    public Set<String> getRolesNames() {
        return this.rolesNames;
    }

    private void setRolesNames(Set<String> roles) {
        this.rolesNames = roles;
    }
}
