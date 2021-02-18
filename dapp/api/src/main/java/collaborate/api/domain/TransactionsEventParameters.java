package collaborate.api.domain;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TransactionsEventParameters<T> implements Serializable {
    @NotNull
    private String entrypoint;

    @NotNull T value;

    public String getEntrypoint() {
        return entrypoint;
    }

    public void setEntrypoint(String entrypoint) {
        this.entrypoint = entrypoint;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
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
