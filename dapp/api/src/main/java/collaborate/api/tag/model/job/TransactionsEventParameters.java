package collaborate.api.tag.model.job;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

public class TransactionsEventParameters<T extends Serializable> implements Serializable {

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
