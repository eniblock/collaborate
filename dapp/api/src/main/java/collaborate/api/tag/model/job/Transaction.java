package collaborate.api.tag.model.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction<T> {

  private String contractAddress;
  private String entryPoint;
  private T entryPointParams;

}
