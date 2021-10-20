package collaborate.api.tag.model.job;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionsEventMessage<T extends Serializable> implements Serializable {

  @NotNull
  private String entrypoint;

  @NotNull
  private String contractAddress;

  @NotNull
  private TransactionsEventParameters<T> parameters;
}
