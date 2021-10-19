package collaborate.api.ipfs.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContentWithCid<T> {

  private String cid;
  private T content;
}
