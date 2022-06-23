package collaborate.api.datasource.model;

import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Embeddable
public class Metadata {

  private String name;
  private String value;
  private String type;
}
