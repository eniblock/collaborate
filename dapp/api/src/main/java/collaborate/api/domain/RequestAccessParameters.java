package collaborate.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class RequestAccessParameters implements Serializable {
    @NotNull
    private String entrypoint;

    @NotNull RequestAccessValue value;

    public String getEntrypoint() {
        return entrypoint;
    }

    public void setEntrypoint(String entrypoint) {
        this.entrypoint = entrypoint;
    }

    public RequestAccessValue getValue() {
        return value;
    }

    public void setValue(RequestAccessValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "RequestAccessParameters{" +
                "entrypoint='" + entrypoint + '\'' +
                ", value=" + value +
                '}';
    }
}
