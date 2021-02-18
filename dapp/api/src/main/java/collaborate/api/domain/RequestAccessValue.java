package collaborate.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class RequestAccessValue implements Serializable {
    @NotNull
    private AccessRequestParams requestAccess;

    public AccessRequestParams getRequestAccess() {
        return requestAccess;
    }

    public void setRequestAccess(AccessRequestParams requestAccess) {
        this.requestAccess = requestAccess;
    }
}
