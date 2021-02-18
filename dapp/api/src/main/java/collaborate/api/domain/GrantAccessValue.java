package collaborate.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class GrantAccessValue implements Serializable {
    @NotNull
    private AccessGrantParams grantAccess;

    public AccessGrantParams getGrantAccess() {
        return grantAccess;
    }

    public void setGrantAccess(AccessGrantParams grantAccess) {
        this.grantAccess = grantAccess;
    }
}
