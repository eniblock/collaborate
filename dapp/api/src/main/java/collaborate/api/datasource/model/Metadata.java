package collaborate.api.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Metadata {

  private String name;
  private String value;
  private String type;
}
