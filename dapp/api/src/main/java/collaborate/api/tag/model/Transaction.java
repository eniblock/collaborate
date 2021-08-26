package collaborate.api.tag.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction<T> {

  private String contractAddress;
  private String entryPoint;
  private T entryPointParams;
}
